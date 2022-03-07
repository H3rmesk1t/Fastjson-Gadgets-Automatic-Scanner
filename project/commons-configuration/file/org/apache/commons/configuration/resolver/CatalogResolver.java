package org.apache.commons.configuration.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.FileSystem;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.resolver.CatalogException;
import org.apache.xml.resolver.readers.CatalogReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CatalogResolver implements EntityResolver {
   private static final int DEBUG_ALL = 9;
   private static final int DEBUG_NORMAL = 4;
   private static final int DEBUG_NONE = 0;
   protected CatalogResolver.CatalogManager manager = new CatalogResolver.CatalogManager();
   protected FileSystem fs = FileSystem.getDefaultFileSystem();
   private org.apache.xml.resolver.tools.CatalogResolver resolver;
   private Log log;

   public CatalogResolver() {
      this.manager.setIgnoreMissingProperties(true);
      this.manager.setUseStaticCatalog(false);
      this.manager.setFileSystem(this.fs);
      this.setLogger((Log)null);
   }

   public void setCatalogFiles(String catalogs) {
      this.manager.setCatalogFiles(catalogs);
   }

   public void setFileSystem(FileSystem fileSystem) {
      this.fs = fileSystem;
      this.manager.setFileSystem(fileSystem);
   }

   public void setBaseDir(String baseDir) {
      this.manager.setBaseDir(baseDir);
   }

   public void setSubstitutor(StrSubstitutor substitutor) {
      this.manager.setSubstitutor(substitutor);
   }

   public void setDebug(boolean debug) {
      if (debug) {
         this.manager.setVerbosity(9);
      } else {
         this.manager.setVerbosity(0);
      }

   }

   public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
      String resolved = this.getResolver().getResolvedEntity(publicId, systemId);
      if (resolved != null) {
         String badFilePrefix = "file://";
         String correctFilePrefix = "file:///";
         if (resolved.startsWith(badFilePrefix) && !resolved.startsWith(correctFilePrefix)) {
            resolved = correctFilePrefix + resolved.substring(badFilePrefix.length());
         }

         try {
            InputStream is = this.fs.getInputStream((String)null, resolved);
            InputSource iSource = new InputSource(resolved);
            iSource.setPublicId(publicId);
            iSource.setByteStream(is);
            return iSource;
         } catch (Exception var8) {
            this.log.warn("Failed to create InputSource for " + resolved + " (" + var8.toString() + ")");
            return null;
         }
      } else {
         return null;
      }
   }

   public Log getLogger() {
      return this.log;
   }

   public void setLogger(Log log) {
      this.log = log != null ? log : LogFactory.getLog(CatalogResolver.class);
   }

   private synchronized org.apache.xml.resolver.tools.CatalogResolver getResolver() {
      if (this.resolver == null) {
         this.resolver = new org.apache.xml.resolver.tools.CatalogResolver(this.manager);
      }

      return this.resolver;
   }

   public static class Catalog extends org.apache.xml.resolver.Catalog {
      private FileSystem fs;
      private FileNameMap fileNameMap = URLConnection.getFileNameMap();

      public void loadSystemCatalogs() throws IOException {
         this.fs = ((CatalogResolver.CatalogManager)this.catalogManager).getFileSystem();
         String base = ((CatalogResolver.CatalogManager)this.catalogManager).getBaseDir();
         Vector catalogs = this.catalogManager.getCatalogFiles();
         if (catalogs != null) {
            for(int count = 0; count < catalogs.size(); ++count) {
               String fileName = (String)catalogs.elementAt(count);
               URL url = null;
               InputStream is = null;

               try {
                  url = ConfigurationUtils.locate(this.fs, base, fileName);
                  if (url != null) {
                     is = this.fs.getInputStream(url);
                  }
               } catch (ConfigurationException var13) {
                  String name = url == null ? fileName : url.toString();
                  this.catalogManager.debug.message(9, "Unable to get input stream for " + name + ". " + var13.getMessage());
               }

               if (is != null) {
                  String mimeType = this.fileNameMap.getContentTypeFor(fileName);

                  try {
                     if (mimeType != null) {
                        this.parseCatalog(mimeType, is);
                        continue;
                     }
                  } catch (Exception var14) {
                     this.catalogManager.debug.message(9, "Exception caught parsing input stream for " + fileName + ". " + var14.getMessage());
                  } finally {
                     is.close();
                  }
               }

               this.parseCatalog(base, fileName);
            }
         }

      }

      public void parseCatalog(String baseDir, String fileName) throws IOException {
         this.base = ConfigurationUtils.locate(this.fs, baseDir, fileName);
         this.catalogCwd = this.base;
         this.default_override = this.catalogManager.getPreferPublic();
         this.catalogManager.debug.message(4, "Parse catalog: " + fileName);
         boolean parsed = false;

         for(int count = 0; !parsed && count < this.readerArr.size(); ++count) {
            CatalogReader reader = (CatalogReader)this.readerArr.get(count);

            InputStream inStream;
            try {
               inStream = this.fs.getInputStream(this.base);
            } catch (Exception var21) {
               this.catalogManager.debug.message(4, "Unable to access " + this.base + var21.getMessage());
               break;
            }

            try {
               reader.readCatalog(this, inStream);
               parsed = true;
            } catch (CatalogException var19) {
               this.catalogManager.debug.message(4, "Parse failed for " + fileName + var19.getMessage());
               if (var19.getExceptionType() == 7) {
                  break;
               }
            } finally {
               try {
                  inStream.close();
               } catch (IOException var18) {
                  inStream = null;
               }

            }
         }

         if (parsed) {
            this.parsePendingCatalogs();
         }

      }

      protected String normalizeURI(String uriref) {
         StrSubstitutor substitutor = ((CatalogResolver.CatalogManager)this.catalogManager).getStrSubstitutor();
         String resolved = substitutor != null ? substitutor.replace(uriref) : uriref;
         return super.normalizeURI(resolved);
      }
   }

   public static class CatalogManager extends org.apache.xml.resolver.CatalogManager {
      private static org.apache.xml.resolver.Catalog staticCatalog;
      private FileSystem fs;
      private String baseDir = System.getProperty("user.dir");
      private StrSubstitutor substitutor;

      public void setFileSystem(FileSystem fileSystem) {
         this.fs = fileSystem;
      }

      public FileSystem getFileSystem() {
         return this.fs;
      }

      public void setBaseDir(String baseDir) {
         if (baseDir != null) {
            this.baseDir = baseDir;
         }

      }

      public String getBaseDir() {
         return this.baseDir;
      }

      public void setSubstitutor(StrSubstitutor substitutor) {
         this.substitutor = substitutor;
      }

      public StrSubstitutor getStrSubstitutor() {
         return this.substitutor;
      }

      public org.apache.xml.resolver.Catalog getPrivateCatalog() {
         org.apache.xml.resolver.Catalog catalog = staticCatalog;
         if (catalog == null || !this.getUseStaticCatalog()) {
            try {
               catalog = new CatalogResolver.Catalog();
               ((org.apache.xml.resolver.Catalog)catalog).setCatalogManager(this);
               ((org.apache.xml.resolver.Catalog)catalog).setupReaders();
               ((org.apache.xml.resolver.Catalog)catalog).loadSystemCatalogs();
            } catch (Exception var3) {
               var3.printStackTrace();
            }

            if (this.getUseStaticCatalog()) {
               staticCatalog = (org.apache.xml.resolver.Catalog)catalog;
            }
         }

         return (org.apache.xml.resolver.Catalog)catalog;
      }

      public org.apache.xml.resolver.Catalog getCatalog() {
         return this.getPrivateCatalog();
      }
   }
}
