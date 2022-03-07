package org.apache.commons.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.UriParser;

public class VFSFileSystem extends DefaultFileSystem {
   public InputStream getInputStream(String basePath, String fileName) throws ConfigurationException {
      try {
         FileSystemManager manager = VFS.getManager();
         FileName path;
         FileName base;
         if (basePath != null) {
            base = manager.resolveURI(basePath);
            path = manager.resolveName(base, fileName);
         } else {
            base = manager.resolveURI(fileName);
            FileName base = base.getParent();
            path = manager.resolveName(base, base.getBaseName());
         }

         FileSystemOptions opts = this.getOptions(path.getScheme());
         FileObject file = opts == null ? manager.resolveFile(path.getURI()) : manager.resolveFile(path.getURI(), opts);
         FileContent content = file.getContent();
         if (content == null) {
            String msg = "Cannot access content of " + file.getName().getFriendlyURI();
            throw new ConfigurationException(msg);
         } else {
            return content.getInputStream();
         }
      } catch (ConfigurationException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new ConfigurationException("Unable to load the configuration file " + fileName, var10);
      }
   }

   public InputStream getInputStream(URL url) throws ConfigurationException {
      try {
         FileSystemOptions opts = this.getOptions(url.getProtocol());
         FileObject file = opts == null ? VFS.getManager().resolveFile(url.toString()) : VFS.getManager().resolveFile(url.toString(), opts);
         if (file.getType() != FileType.FILE) {
            throw new ConfigurationException("Cannot load a configuration from a directory");
         } else {
            FileContent content = file.getContent();
            if (content == null) {
               String msg = "Cannot access content of " + file.getName().getFriendlyURI();
               throw new ConfigurationException(msg);
            } else {
               return content.getInputStream();
            }
         }
      } catch (FileSystemException var6) {
         String msg = "Unable to access " + url.toString();
         throw new ConfigurationException(msg, var6);
      }
   }

   public OutputStream getOutputStream(URL url) throws ConfigurationException {
      try {
         FileSystemOptions opts = this.getOptions(url.getProtocol());
         FileSystemManager fsManager = VFS.getManager();
         FileObject file = opts == null ? fsManager.resolveFile(url.toString()) : fsManager.resolveFile(url.toString(), opts);
         if (file != null && file.getType() != FileType.FOLDER) {
            FileContent content = file.getContent();
            if (content == null) {
               throw new ConfigurationException("Cannot access content of " + url);
            } else {
               return content.getOutputStream();
            }
         } else {
            throw new ConfigurationException("Cannot save a configuration to a directory");
         }
      } catch (FileSystemException var6) {
         throw new ConfigurationException("Unable to access " + url, var6);
      }
   }

   public String getPath(File file, URL url, String basePath, String fileName) {
      if (file != null) {
         return super.getPath(file, url, basePath, fileName);
      } else {
         try {
            FileSystemManager fsManager = VFS.getManager();
            FileName name;
            if (url != null) {
               name = fsManager.resolveURI(url.toString());
               if (name != null) {
                  return name.toString();
               }
            }

            if (UriParser.extractScheme(fileName) != null) {
               return fileName;
            } else if (basePath != null) {
               name = fsManager.resolveURI(basePath);
               return fsManager.resolveName(name, fileName).getURI();
            } else {
               name = fsManager.resolveURI(fileName);
               FileName base = name.getParent();
               return fsManager.resolveName(base, name.getBaseName()).getURI();
            }
         } catch (FileSystemException var8) {
            var8.printStackTrace();
            return null;
         }
      }
   }

   public String getBasePath(String path) {
      if (UriParser.extractScheme(path) == null) {
         return super.getBasePath(path);
      } else {
         try {
            FileSystemManager fsManager = VFS.getManager();
            FileName name = fsManager.resolveURI(path);
            return name.getParent().getURI();
         } catch (FileSystemException var4) {
            var4.printStackTrace();
            return null;
         }
      }
   }

   public String getFileName(String path) {
      if (UriParser.extractScheme(path) == null) {
         return super.getFileName(path);
      } else {
         try {
            FileSystemManager fsManager = VFS.getManager();
            FileName name = fsManager.resolveURI(path);
            return name.getBaseName();
         } catch (FileSystemException var4) {
            var4.printStackTrace();
            return null;
         }
      }
   }

   public URL getURL(String basePath, String file) throws MalformedURLException {
      if ((basePath == null || UriParser.extractScheme(basePath) != null) && (basePath != null || UriParser.extractScheme(file) != null)) {
         try {
            FileSystemManager fsManager = VFS.getManager();
            FileName path;
            if (basePath != null && UriParser.extractScheme(file) == null) {
               FileName base = fsManager.resolveURI(basePath);
               path = fsManager.resolveName(base, file);
            } else {
               path = fsManager.resolveURI(file);
            }

            URLStreamHandler handler = new VFSFileSystem.VFSURLStreamHandler(path);
            return new URL((URL)null, path.getURI(), handler);
         } catch (FileSystemException var6) {
            throw new ConfigurationRuntimeException("Could not parse basePath: " + basePath + " and fileName: " + file, var6);
         }
      } else {
         return super.getURL(basePath, file);
      }
   }

   public URL locateFromURL(String basePath, String fileName) {
      String fileScheme = UriParser.extractScheme(fileName);
      if ((basePath == null || UriParser.extractScheme(basePath) == null) && fileScheme == null) {
         return super.locateFromURL(basePath, fileName);
      } else {
         try {
            FileSystemManager fsManager = VFS.getManager();
            FileObject file;
            if (basePath != null && fileScheme == null) {
               String scheme = UriParser.extractScheme(basePath);
               FileSystemOptions opts = scheme != null ? this.getOptions(scheme) : null;
               FileObject base = opts == null ? fsManager.resolveFile(basePath) : fsManager.resolveFile(basePath, opts);
               if (base.getType() == FileType.FILE) {
                  base = base.getParent();
               }

               file = fsManager.resolveFile(base, fileName);
            } else {
               FileSystemOptions opts = fileScheme != null ? this.getOptions(fileScheme) : null;
               file = opts == null ? fsManager.resolveFile(fileName) : fsManager.resolveFile(fileName, opts);
            }

            if (!file.exists()) {
               return null;
            } else {
               FileName path = file.getName();
               URLStreamHandler handler = new VFSFileSystem.VFSURLStreamHandler(path);
               return new URL((URL)null, path.getURI(), handler);
            }
         } catch (FileSystemException var9) {
            return null;
         } catch (MalformedURLException var10) {
            return null;
         }
      }
   }

   private FileSystemOptions getOptions(String scheme) {
      FileSystemOptions opts = new FileSystemOptions();

      FileSystemConfigBuilder builder;
      try {
         builder = VFS.getManager().getFileSystemConfigBuilder(scheme);
      } catch (Exception var11) {
         return null;
      }

      FileOptionsProvider provider = this.getFileOptionsProvider();
      if (provider != null) {
         Map map = provider.getOptions();
         if (map == null) {
            return null;
         }

         int count = 0;
         Iterator i$ = map.entrySet().iterator();

         while(i$.hasNext()) {
            Entry entry = (Entry)i$.next();

            try {
               String key = (String)entry.getKey();
               if ("currentUser".equals(key)) {
                  key = "creatorName";
               }

               this.setProperty(builder, opts, key, entry.getValue());
               ++count;
            } catch (Exception var10) {
            }
         }

         if (count > 0) {
            return opts;
         }
      }

      return null;
   }

   private void setProperty(FileSystemConfigBuilder builder, FileSystemOptions options, String key, Object value) {
      String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
      Class[] paramTypes = new Class[]{FileSystemOptions.class, value.getClass()};

      try {
         Method method = builder.getClass().getMethod(methodName, paramTypes);
         Object[] params = new Object[]{options, value};
         method.invoke(builder, params);
      } catch (Exception var9) {
      }
   }

   private static class VFSURLStreamHandler extends URLStreamHandler {
      private final String protocol;

      public VFSURLStreamHandler(FileName file) {
         this.protocol = file.getScheme();
      }

      protected URLConnection openConnection(URL url) throws IOException {
         throw new IOException("VFS URLs can only be used with VFS APIs");
      }
   }
}
