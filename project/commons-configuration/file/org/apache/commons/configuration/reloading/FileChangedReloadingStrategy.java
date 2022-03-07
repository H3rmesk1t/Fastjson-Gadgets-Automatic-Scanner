package org.apache.commons.configuration.reloading;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileChangedReloadingStrategy implements ReloadingStrategy {
   private static final String JAR_PROTOCOL = "jar";
   private static final int DEFAULT_REFRESH_DELAY = 5000;
   protected FileConfiguration configuration;
   protected long lastModified;
   protected long lastChecked;
   protected long refreshDelay = 5000L;
   private boolean reloading;
   private Log logger = LogFactory.getLog(FileChangedReloadingStrategy.class);

   public void setConfiguration(FileConfiguration configuration) {
      this.configuration = configuration;
   }

   public void init() {
      this.updateLastModified();
   }

   public boolean reloadingRequired() {
      if (!this.reloading) {
         long now = System.currentTimeMillis();
         if (now > this.lastChecked + this.refreshDelay) {
            this.lastChecked = now;
            if (this.hasChanged()) {
               if (this.logger.isDebugEnabled()) {
                  this.logger.debug("File change detected: " + this.getName());
               }

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
      File file = this.getFile();
      if (file != null) {
         this.lastModified = file.lastModified();
      }

      this.reloading = false;
   }

   protected boolean hasChanged() {
      File file = this.getFile();
      if (file != null && file.exists()) {
         return file.lastModified() > this.lastModified;
      } else {
         if (this.logger.isWarnEnabled() && this.lastModified != 0L) {
            this.logger.warn("File was deleted: " + this.getName(file));
            this.lastModified = 0L;
         }

         return false;
      }
   }

   protected File getFile() {
      return this.configuration.getURL() != null ? this.fileFromURL(this.configuration.getURL()) : this.configuration.getFile();
   }

   private File fileFromURL(URL url) {
      if ("jar".equals(url.getProtocol())) {
         String path = url.getPath();

         try {
            return ConfigurationUtils.fileFromURL(new URL(path.substring(0, path.indexOf(33))));
         } catch (MalformedURLException var4) {
            return null;
         }
      } else {
         return ConfigurationUtils.fileFromURL(url);
      }
   }

   private String getName() {
      return this.getName(this.getFile());
   }

   private String getName(File file) {
      String name = this.configuration.getURL().toString();
      if (name == null) {
         if (file != null) {
            name = file.getAbsolutePath();
         } else {
            name = "base: " + this.configuration.getBasePath() + "file: " + this.configuration.getFileName();
         }
      }

      return name;
   }
}
