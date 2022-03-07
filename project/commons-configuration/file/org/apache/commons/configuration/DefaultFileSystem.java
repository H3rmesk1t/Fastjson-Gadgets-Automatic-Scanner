package org.apache.commons.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultFileSystem extends FileSystem {
   private Log log = LogFactory.getLog(DefaultFileSystem.class);

   public InputStream getInputStream(String basePath, String fileName) throws ConfigurationException {
      try {
         URL url = ConfigurationUtils.locate(this, basePath, fileName);
         if (url == null) {
            throw new ConfigurationException("Cannot locate configuration source " + fileName);
         } else {
            return this.getInputStream(url);
         }
      } catch (ConfigurationException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new ConfigurationException("Unable to load the configuration file " + fileName, var5);
      }
   }

   public InputStream getInputStream(URL url) throws ConfigurationException {
      File file = ConfigurationUtils.fileFromURL(url);
      if (file != null && file.isDirectory()) {
         throw new ConfigurationException("Cannot load a configuration from a directory");
      } else {
         try {
            return url.openStream();
         } catch (Exception var4) {
            throw new ConfigurationException("Unable to load the configuration from the URL " + url, var4);
         }
      }
   }

   public OutputStream getOutputStream(URL url) throws ConfigurationException {
      File file = ConfigurationUtils.fileFromURL(url);
      if (file != null) {
         return this.getOutputStream(file);
      } else {
         try {
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            if (connection instanceof HttpURLConnection) {
               HttpURLConnection conn = (HttpURLConnection)connection;
               conn.setRequestMethod("PUT");
            }

            OutputStream out = connection.getOutputStream();
            if (connection instanceof HttpURLConnection) {
               out = new DefaultFileSystem.HttpOutputStream((OutputStream)out, (HttpURLConnection)connection);
            }

            return (OutputStream)out;
         } catch (IOException var6) {
            throw new ConfigurationException("Could not save to URL " + url, var6);
         }
      }
   }

   public OutputStream getOutputStream(File file) throws ConfigurationException {
      try {
         this.createPath(file);
         return new FileOutputStream(file);
      } catch (FileNotFoundException var3) {
         throw new ConfigurationException("Unable to save to file " + file, var3);
      }
   }

   public String getPath(File file, URL url, String basePath, String fileName) {
      String path = null;
      if (file != null) {
         path = file.getAbsolutePath();
      }

      if (path == null) {
         if (url != null) {
            path = url.getPath();
         } else {
            try {
               path = this.getURL(basePath, fileName).getPath();
            } catch (Exception var7) {
               if (this.log.isDebugEnabled()) {
                  this.log.debug(String.format("Could not determine URL for basePath = %s, fileName = %s.", basePath, fileName), var7);
               }
            }
         }
      }

      return path;
   }

   public String getBasePath(String path) {
      try {
         URL url = this.getURL((String)null, path);
         return ConfigurationUtils.getBasePath(url);
      } catch (Exception var4) {
         return null;
      }
   }

   public String getFileName(String path) {
      try {
         URL url = this.getURL((String)null, path);
         return ConfigurationUtils.getFileName(url);
      } catch (Exception var4) {
         return null;
      }
   }

   public URL getURL(String basePath, String file) throws MalformedURLException {
      File f = new File(file);
      if (f.isAbsolute()) {
         return ConfigurationUtils.toURL(f);
      } else {
         try {
            if (basePath == null) {
               return new URL(file);
            } else {
               URL base = new URL(basePath);
               return new URL(base, file);
            }
         } catch (MalformedURLException var5) {
            return ConfigurationUtils.toURL(ConfigurationUtils.constructFile(basePath, file));
         }
      }
   }

   public URL locateFromURL(String basePath, String fileName) {
      try {
         if (basePath == null) {
            return new URL(fileName);
         } else {
            URL baseURL = new URL(basePath);
            URL url = new URL(baseURL, fileName);
            InputStream in = null;

            try {
               in = url.openStream();
            } finally {
               if (in != null) {
                  in.close();
               }

            }

            return url;
         }
      } catch (IOException var10) {
         if (this.log.isDebugEnabled()) {
            this.log.debug("Could not locate file " + fileName + " at " + basePath + ": " + var10.getMessage());
         }

         return null;
      }
   }

   private void createPath(File file) {
      if (file != null && !file.exists()) {
         File parent = file.getParentFile();
         if (parent != null && !parent.exists()) {
            parent.mkdirs();
         }
      }

   }

   private static class HttpOutputStream extends VerifiableOutputStream {
      private final OutputStream stream;
      private final HttpURLConnection connection;

      public HttpOutputStream(OutputStream stream, HttpURLConnection connection) {
         this.stream = stream;
         this.connection = connection;
      }

      public void write(byte[] bytes) throws IOException {
         this.stream.write(bytes);
      }

      public void write(byte[] bytes, int i, int i1) throws IOException {
         this.stream.write(bytes, i, i1);
      }

      public void flush() throws IOException {
         this.stream.flush();
      }

      public void close() throws IOException {
         this.stream.close();
      }

      public void write(int i) throws IOException {
         this.stream.write(i);
      }

      public String toString() {
         return this.stream.toString();
      }

      public void verify() throws IOException {
         if (this.connection.getResponseCode() >= 400) {
            throw new IOException("HTTP Error " + this.connection.getResponseCode() + " " + this.connection.getResponseMessage());
         }
      }
   }
}
