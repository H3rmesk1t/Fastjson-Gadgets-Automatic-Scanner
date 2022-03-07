package org.apache.commons.configuration.reloading;

import java.io.File;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.FileSystem;
import org.apache.commons.configuration.FileSystemBased;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class VFSFileChangedReloadingStrategy implements ReloadingStrategy {
   private static final int DEFAULT_REFRESH_DELAY = 5000;
   protected FileConfiguration configuration;
   protected long lastModified;
   protected long lastChecked;
   protected long refreshDelay = 5000L;
   private boolean reloading;
   private Log log = LogFactory.getLog(this.getClass());

   public void setConfiguration(FileConfiguration configuration) {
      this.configuration = configuration;
   }

   public void init() {
      if (this.configuration.getURL() != null || this.configuration.getFileName() != null) {
         if (this.configuration == null) {
            throw new IllegalStateException("No configuration has been set for this strategy");
         } else {
            this.updateLastModified();
         }
      }
   }

   public boolean reloadingRequired() {
      if (!this.reloading) {
         long now = System.currentTimeMillis();
         if (now > this.lastChecked + this.refreshDelay) {
            this.lastChecked = now;
            if (this.hasChanged()) {
               this.reloading = true;
            }
         }
      }

      return this.reloading;
   }

   public void reloadingPerformed() {
      this.updateLastModified();
   }

   public long getRefreshDelay() {
      return this.refreshDelay;
   }

   public void setRefreshDelay(long refreshDelay) {
      this.refreshDelay = refreshDelay;
   }

   protected void updateLastModified() {
      FileObject file = this.getFile();
      if (file != null) {
         try {
            this.lastModified = file.getContent().getLastModifiedTime();
         } catch (FileSystemException var3) {
            this.log.error("Unable to get last modified time for" + file.getName().getURI());
         }
      }

      this.reloading = false;
   }

   protected boolean hasChanged() {
      FileObject file = this.getFile();

      try {
         if (file != null && file.exists()) {
            return file.getContent().getLastModifiedTime() > this.lastModified;
         } else {
            return false;
         }
      } catch (FileSystemException var3) {
         this.log.error("Unable to get last modified time for" + file.getName().getURI());
         return false;
      }
   }

   protected FileObject getFile() {
      try {
         FileSystemManager fsManager = VFS.getManager();
         FileSystem fs = ((FileSystemBased)this.configuration).getFileSystem();
         String uri = fs.getPath((File)null, this.configuration.getURL(), this.configuration.getBasePath(), this.configuration.getFileName());
         if (uri == null) {
            throw new ConfigurationRuntimeException("Unable to determine file to monitor");
         } else {
            return fsManager.resolveFile(uri);
         }
      } catch (FileSystemException var4) {
         String msg = "Unable to monitor " + this.configuration.getURL().toString();
         this.log.error(msg);
         throw new ConfigurationRuntimeException(msg, var4);
      }
   }
}
