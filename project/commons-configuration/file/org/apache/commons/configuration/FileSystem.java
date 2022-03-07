package org.apache.commons.configuration;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.NoOpLog;

public abstract class FileSystem {
   private static final String FILE_SYSTEM = "org.apache.commons.configuration.filesystem";
   private static FileSystem fileSystem;
   private Log log;
   private FileOptionsProvider optionsProvider;

   public FileSystem() {
      this.setLogger((Log)null);
   }

   public Log getLogger() {
      return this.log;
   }

   public void setLogger(Log log) {
      this.log = (Log)(log != null ? log : new NoOpLog());
   }

   public static void setDefaultFileSystem(FileSystem fs) throws NullPointerException {
      if (fs == null) {
         throw new NullPointerException("A FileSystem implementation is required");
      } else {
         fileSystem = fs;
      }
   }

   public static void resetDefaultFileSystem() {
      fileSystem = new DefaultFileSystem();
   }

   public static FileSystem getDefaultFileSystem() {
      return fileSystem;
   }

   public void setFileOptionsProvider(FileOptionsProvider provider) {
      this.optionsProvider = provider;
   }

   public FileOptionsProvider getFileOptionsProvider() {
      return this.optionsProvider;
   }

   public abstract InputStream getInputStream(String var1, String var2) throws ConfigurationException;

   public abstract InputStream getInputStream(URL var1) throws ConfigurationException;

   public abstract OutputStream getOutputStream(URL var1) throws ConfigurationException;

   public abstract OutputStream getOutputStream(File var1) throws ConfigurationException;

   public abstract String getPath(File var1, URL var2, String var3, String var4);

   public abstract String getBasePath(String var1);

   public abstract String getFileName(String var1);

   public abstract URL locateFromURL(String var1, String var2);

   public abstract URL getURL(String var1, String var2) throws MalformedURLException;

   static {
      String fsClassName = System.getProperty("org.apache.commons.configuration.filesystem");
      if (fsClassName != null) {
         Log log = LogFactory.getLog(FileSystem.class);

         try {
            Class clazz = Class.forName(fsClassName);
            if (FileSystem.class.isAssignableFrom(clazz)) {
               fileSystem = (FileSystem)clazz.newInstance();
               if (log.isDebugEnabled()) {
                  log.debug("Using " + fsClassName);
               }
            }
         } catch (InstantiationException var3) {
            log.error("Unable to create " + fsClassName, var3);
         } catch (IllegalAccessException var4) {
            log.error("Unable to create " + fsClassName, var4);
         } catch (ClassNotFoundException var5) {
            log.error("Unable to create " + fsClassName, var5);
         }
      }

      if (fileSystem == null) {
         fileSystem = new DefaultFileSystem();
      }

   }
}
