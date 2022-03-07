package org.apache.commons.configuration;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import org.apache.commons.configuration.event.ConfigurationErrorEvent;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.EventSource;
import org.apache.commons.configuration.reloading.Reloadable;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ConfigurationUtils {
   static final String PROTOCOL_FILE = "file";
   static final String RESOURCE_PATH_SEPARATOR = "/";
   private static final String FILE_SCHEME = "file:";
   private static final String METHOD_CLONE = "clone";
   private static final int HEX = 16;
   private static final Log LOG = LogFactory.getLog(ConfigurationUtils.class);

   private ConfigurationUtils() {
   }

   public static void dump(Configuration configuration, PrintStream out) {
      dump(configuration, new PrintWriter(out));
   }

   public static void dump(Configuration configuration, PrintWriter out) {
      Iterator keys = configuration.getKeys();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         Object value = configuration.getProperty(key);
         out.print(key);
         out.print("=");
         out.print(value);
         if (keys.hasNext()) {
            out.println();
         }
      }

      out.flush();
   }

   public static String toString(Configuration configuration) {
      StringWriter writer = new StringWriter();
      dump(configuration, new PrintWriter(writer));
      return writer.toString();
   }

   public static void copy(Configuration source, Configuration target) {
      Iterator keys = source.getKeys();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         target.setProperty(key, source.getProperty(key));
      }

   }

   public static void append(Configuration source, Configuration target) {
      Iterator keys = source.getKeys();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         target.addProperty(key, source.getProperty(key));
      }

   }

   public static HierarchicalConfiguration convertToHierarchical(Configuration conf) {
      return convertToHierarchical(conf, (ExpressionEngine)null);
   }

   public static HierarchicalConfiguration convertToHierarchical(Configuration conf, ExpressionEngine engine) {
      if (conf == null) {
         return null;
      } else {
         HierarchicalConfiguration hc;
         if (conf instanceof HierarchicalConfiguration) {
            if (conf instanceof Reloadable) {
               Object lock = ((Reloadable)conf).getReloadLock();
               synchronized(lock) {
                  hc = new HierarchicalConfiguration((HierarchicalConfiguration)conf);
               }
            } else {
               hc = (HierarchicalConfiguration)conf;
            }

            if (engine != null) {
               hc.setExpressionEngine(engine);
            }

            return hc;
         } else {
            hc = new HierarchicalConfiguration();
            if (engine != null) {
               hc.setExpressionEngine(engine);
            }

            boolean delimiterParsingStatus = hc.isDelimiterParsingDisabled();
            hc.setDelimiterParsingDisabled(true);
            hc.append(conf);
            hc.setDelimiterParsingDisabled(delimiterParsingStatus);
            return hc;
         }
      }
   }

   public static Configuration cloneConfiguration(Configuration config) throws ConfigurationRuntimeException {
      if (config == null) {
         return null;
      } else {
         try {
            return (Configuration)clone(config);
         } catch (CloneNotSupportedException var2) {
            throw new ConfigurationRuntimeException(var2);
         }
      }
   }

   static Object clone(Object obj) throws CloneNotSupportedException {
      if (obj instanceof Cloneable) {
         try {
            Method m = obj.getClass().getMethod("clone");
            return m.invoke(obj);
         } catch (NoSuchMethodException var2) {
            throw new CloneNotSupportedException("No clone() method found for class" + obj.getClass().getName());
         } catch (IllegalAccessException var3) {
            throw new ConfigurationRuntimeException(var3);
         } catch (InvocationTargetException var4) {
            throw new ConfigurationRuntimeException(var4);
         }
      } else {
         throw new CloneNotSupportedException(obj.getClass().getName() + " does not implement Cloneable");
      }
   }

   public static URL getURL(String basePath, String file) throws MalformedURLException {
      return FileSystem.getDefaultFileSystem().getURL(basePath, file);
   }

   static File constructFile(String basePath, String fileName) {
      File absolute = null;
      if (fileName != null) {
         absolute = new File(fileName);
      }

      File file;
      if (StringUtils.isEmpty(basePath) || absolute != null && absolute.isAbsolute()) {
         file = new File(fileName);
      } else {
         StringBuilder fName = new StringBuilder();
         fName.append(basePath);
         if (!basePath.endsWith(File.separator)) {
            fName.append(File.separator);
         }

         if (fileName.startsWith("." + File.separator)) {
            fName.append(fileName.substring(2));
         } else {
            fName.append(fileName);
         }

         file = new File(fName.toString());
      }

      return file;
   }

   public static URL locate(String name) {
      return locate((String)null, name);
   }

   public static URL locate(String base, String name) {
      return locate(FileSystem.getDefaultFileSystem(), base, name);
   }

   public static URL locate(FileSystem fileSystem, String base, String name) {
      if (LOG.isDebugEnabled()) {
         StringBuilder buf = new StringBuilder();
         buf.append("ConfigurationUtils.locate(): base is ").append(base);
         buf.append(", name is ").append(name);
         LOG.debug(buf.toString());
      }

      if (name == null) {
         return null;
      } else {
         URL url = fileSystem.locateFromURL(base, name);
         File file;
         if (url == null) {
            file = new File(name);
            if (file.isAbsolute() && file.exists()) {
               try {
                  url = toURL(file);
                  LOG.debug("Loading configuration from the absolute path " + name);
               } catch (MalformedURLException var8) {
                  LOG.warn("Could not obtain URL from file", var8);
               }
            }
         }

         if (url == null) {
            try {
               file = constructFile(base, name);
               if (file != null && file.exists()) {
                  url = toURL(file);
               }

               if (url != null) {
                  LOG.debug("Loading configuration from the path " + file);
               }
            } catch (MalformedURLException var7) {
               LOG.warn("Could not obtain URL from file", var7);
            }
         }

         if (url == null) {
            try {
               file = constructFile(System.getProperty("user.home"), name);
               if (file != null && file.exists()) {
                  url = toURL(file);
               }

               if (url != null) {
                  LOG.debug("Loading configuration from the home path " + file);
               }
            } catch (MalformedURLException var6) {
               LOG.warn("Could not obtain URL from file", var6);
            }
         }

         if (url == null) {
            url = locateFromClasspath(name);
         }

         return url;
      }
   }

   static URL locateFromClasspath(String resourceName) {
      URL url = null;
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      if (loader != null) {
         url = loader.getResource(resourceName);
         if (url != null) {
            LOG.debug("Loading configuration from the context classpath (" + resourceName + ")");
         }
      }

      if (url == null) {
         url = ClassLoader.getSystemResource(resourceName);
         if (url != null) {
            LOG.debug("Loading configuration from the system classpath (" + resourceName + ")");
         }
      }

      return url;
   }

   static String getBasePath(URL url) {
      if (url == null) {
         return null;
      } else {
         String s = url.toString();
         if (s.startsWith("file:") && !s.startsWith("file://")) {
            s = "file://" + s.substring("file:".length());
         }

         return !s.endsWith("/") && !StringUtils.isEmpty(url.getPath()) ? s.substring(0, s.lastIndexOf("/") + 1) : s;
      }
   }

   static String getFileName(URL url) {
      if (url == null) {
         return null;
      } else {
         String path = url.getPath();
         return !path.endsWith("/") && !StringUtils.isEmpty(path) ? path.substring(path.lastIndexOf("/") + 1) : null;
      }
   }

   public static File getFile(String basePath, String fileName) {
      File f = new File(fileName);
      if (f.isAbsolute()) {
         return f;
      } else {
         URL url;
         try {
            url = new URL(new URL(basePath), fileName);
         } catch (MalformedURLException var7) {
            try {
               url = new URL(fileName);
            } catch (MalformedURLException var6) {
               url = null;
            }
         }

         return url != null ? fileFromURL(url) : constructFile(basePath, fileName);
      }
   }

   public static File fileFromURL(URL url) {
      if (url != null && url.getProtocol().equals("file")) {
         String filename = url.getFile().replace('/', File.separatorChar);
         int pos = 0;

         while((pos = filename.indexOf(37, pos)) >= 0) {
            if (pos + 2 < filename.length()) {
               String hexStr = filename.substring(pos + 1, pos + 3);
               char ch = (char)Integer.parseInt(hexStr, 16);
               filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
            }
         }

         return new File(filename);
      } else {
         return null;
      }
   }

   static URL toURL(File file) throws MalformedURLException {
      return file.toURI().toURL();
   }

   public static void enableRuntimeExceptions(Configuration src) {
      if (!(src instanceof EventSource)) {
         throw new IllegalArgumentException("Configuration must be derived from EventSource!");
      } else {
         ((EventSource)src).addErrorListener(new ConfigurationErrorListener() {
            public void configurationError(ConfigurationErrorEvent event) {
               throw new ConfigurationRuntimeException(event.getCause());
            }
         });
      }
   }
}
