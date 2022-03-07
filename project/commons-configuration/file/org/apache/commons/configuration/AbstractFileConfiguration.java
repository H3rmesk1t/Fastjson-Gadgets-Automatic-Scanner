package org.apache.commons.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractFileConfiguration extends BaseConfiguration implements FileConfiguration, FileSystemBased {
   public static final int EVENT_RELOAD = 20;
   public static final int EVENT_CONFIG_CHANGED = 21;
   private static final String FILE_SCHEME = "file:";
   protected String fileName;
   protected String basePath;
   protected boolean autoSave;
   protected ReloadingStrategy strategy;
   protected Object reloadLock;
   private String encoding;
   private URL sourceURL;
   private int noReload;
   private FileSystem fileSystem;

   public AbstractFileConfiguration() {
      this.reloadLock = new Lock("AbstractFileConfiguration");
      this.fileSystem = FileSystem.getDefaultFileSystem();
      this.initReloadingStrategy();
      this.setLogger(LogFactory.getLog(this.getClass()));
      this.addErrorLogListener();
   }

   public AbstractFileConfiguration(String fileName) throws ConfigurationException {
      this();
      this.setFileName(fileName);
      this.load();
   }

   public AbstractFileConfiguration(File file) throws ConfigurationException {
      this();
      this.setFile(file);
      if (file.exists()) {
         this.load();
      }

   }

   public AbstractFileConfiguration(URL url) throws ConfigurationException {
      this();
      this.setURL(url);
      this.load();
   }

   public void setFileSystem(FileSystem fileSystem) {
      if (fileSystem == null) {
         throw new NullPointerException("A valid FileSystem must be specified");
      } else {
         this.fileSystem = fileSystem;
      }
   }

   public void resetFileSystem() {
      this.fileSystem = FileSystem.getDefaultFileSystem();
   }

   public FileSystem getFileSystem() {
      return this.fileSystem;
   }

   public Object getReloadLock() {
      return this.reloadLock;
   }

   public void load() throws ConfigurationException {
      if (this.sourceURL != null) {
         this.load(this.sourceURL);
      } else {
         this.load(this.getFileName());
      }

   }

   public void load(String fileName) throws ConfigurationException {
      try {
         URL url = ConfigurationUtils.locate(this.fileSystem, this.basePath, fileName);
         if (url == null) {
            throw new ConfigurationException("Cannot locate configuration source " + fileName);
         } else {
            this.load(url);
         }
      } catch (ConfigurationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new ConfigurationException("Unable to load the configuration file " + fileName, var4);
      }
   }

   public void load(File file) throws ConfigurationException {
      try {
         this.load(ConfigurationUtils.toURL(file));
      } catch (ConfigurationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new ConfigurationException("Unable to load the configuration file " + file, var4);
      }
   }

   public void load(URL url) throws ConfigurationException {
      if (this.sourceURL == null) {
         if (StringUtils.isEmpty(this.getBasePath())) {
            this.setBasePath(url.toString());
         }

         this.sourceURL = url;
      }

      InputStream in = null;

      try {
         in = this.fileSystem.getInputStream(url);
         this.load(in);
      } catch (ConfigurationException var12) {
         throw var12;
      } catch (Exception var13) {
         throw new ConfigurationException("Unable to load the configuration from the URL " + url, var13);
      } finally {
         try {
            if (in != null) {
               in.close();
            }
         } catch (IOException var11) {
            this.getLogger().warn("Could not close input stream", var11);
         }

      }

   }

   public void load(InputStream in) throws ConfigurationException {
      this.load(in, this.getEncoding());
   }

   public void load(InputStream in, String encoding) throws ConfigurationException {
      Reader reader = null;
      if (encoding != null) {
         try {
            reader = new InputStreamReader(in, encoding);
         } catch (UnsupportedEncodingException var5) {
            throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", var5);
         }
      }

      if (reader == null) {
         reader = new InputStreamReader(in);
      }

      this.load((Reader)reader);
   }

   public void save() throws ConfigurationException {
      if (this.getFileName() == null) {
         throw new ConfigurationException("No file name has been set!");
      } else {
         if (this.sourceURL != null) {
            this.save(this.sourceURL);
         } else {
            this.save(this.fileName);
         }

         this.strategy.init();
      }
   }

   public void save(String fileName) throws ConfigurationException {
      try {
         URL url = this.fileSystem.getURL(this.basePath, fileName);
         if (url == null) {
            throw new ConfigurationException("Cannot locate configuration source " + fileName);
         } else {
            this.save(url);
         }
      } catch (ConfigurationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new ConfigurationException("Unable to save the configuration to the file " + fileName, var4);
      }
   }

   public void save(URL url) throws ConfigurationException {
      OutputStream out = null;

      try {
         out = this.fileSystem.getOutputStream(url);
         this.save(out);
         if (out instanceof VerifiableOutputStream) {
            ((VerifiableOutputStream)out).verify();
         }
      } catch (IOException var7) {
         throw new ConfigurationException("Could not save to URL " + url, var7);
      } finally {
         this.closeSilent(out);
      }

   }

   public void save(File file) throws ConfigurationException {
      OutputStream out = null;

      try {
         out = this.fileSystem.getOutputStream(file);
         this.save(out);
      } finally {
         this.closeSilent(out);
      }

   }

   public void save(OutputStream out) throws ConfigurationException {
      this.save(out, this.getEncoding());
   }

   public void save(OutputStream out, String encoding) throws ConfigurationException {
      Writer writer = null;
      if (encoding != null) {
         try {
            writer = new OutputStreamWriter(out, encoding);
         } catch (UnsupportedEncodingException var5) {
            throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", var5);
         }
      }

      if (writer == null) {
         writer = new OutputStreamWriter(out);
      }

      this.save((Writer)writer);
   }

   public String getFileName() {
      return this.fileName;
   }

   public void setFileName(String fileName) {
      if (fileName != null && fileName.startsWith("file:") && !fileName.startsWith("file://")) {
         fileName = "file://" + fileName.substring("file:".length());
      }

      this.sourceURL = null;
      this.fileName = fileName;
      this.getLogger().debug("FileName set to " + fileName);
   }

   public String getBasePath() {
      return this.basePath;
   }

   public void setBasePath(String basePath) {
      if (basePath != null && basePath.startsWith("file:") && !basePath.startsWith("file://")) {
         basePath = "file://" + basePath.substring("file:".length());
      }

      this.sourceURL = null;
      this.basePath = basePath;
      this.getLogger().debug("Base path set to " + basePath);
   }

   public File getFile() {
      if (this.getFileName() == null && this.sourceURL == null) {
         return null;
      } else {
         return this.sourceURL != null ? ConfigurationUtils.fileFromURL(this.sourceURL) : ConfigurationUtils.getFile(this.getBasePath(), this.getFileName());
      }
   }

   public void setFile(File file) {
      this.sourceURL = null;
      this.setFileName(file.getName());
      this.setBasePath(file.getParentFile() != null ? file.getParentFile().getAbsolutePath() : null);
   }

   public String getPath() {
      return this.fileSystem.getPath(this.getFile(), this.sourceURL, this.getBasePath(), this.getFileName());
   }

   public void setPath(String path) {
      this.setFile(new File(path));
   }

   URL getSourceURL() {
      return this.sourceURL;
   }

   public URL getURL() {
      return this.sourceURL != null ? this.sourceURL : ConfigurationUtils.locate(this.fileSystem, this.getBasePath(), this.getFileName());
   }

   public void setURL(URL url) {
      this.setBasePath(ConfigurationUtils.getBasePath(url));
      this.setFileName(ConfigurationUtils.getFileName(url));
      this.sourceURL = url;
      this.getLogger().debug("URL set to " + url);
   }

   public void setAutoSave(boolean autoSave) {
      this.autoSave = autoSave;
   }

   public boolean isAutoSave() {
      return this.autoSave;
   }

   protected void possiblySave() {
      if (this.autoSave && this.fileName != null) {
         try {
            this.save();
         } catch (ConfigurationException var2) {
            throw new ConfigurationRuntimeException("Failed to auto-save", var2);
         }
      }

   }

   public void addProperty(String key, Object value) {
      synchronized(this.reloadLock) {
         super.addProperty(key, value);
         this.possiblySave();
      }
   }

   public void setProperty(String key, Object value) {
      synchronized(this.reloadLock) {
         super.setProperty(key, value);
         this.possiblySave();
      }
   }

   public void clearProperty(String key) {
      synchronized(this.reloadLock) {
         super.clearProperty(key);
         this.possiblySave();
      }
   }

   public ReloadingStrategy getReloadingStrategy() {
      return this.strategy;
   }

   public void setReloadingStrategy(ReloadingStrategy strategy) {
      this.strategy = strategy;
      strategy.setConfiguration(this);
      strategy.init();
   }

   public void reload() {
      this.reload(false);
   }

   public boolean reload(boolean checkReload) {
      synchronized(this.reloadLock) {
         if (this.noReload == 0) {
            boolean var4;
            try {
               this.enterNoReload();
               if (this.strategy.reloadingRequired()) {
                  if (this.getLogger().isInfoEnabled()) {
                     this.getLogger().info("Reloading configuration. URL is " + this.getURL());
                  }

                  this.refresh();
                  this.strategy.reloadingPerformed();
               }

               return true;
            } catch (Exception var10) {
               this.fireError(20, (String)null, (Object)null, var10);
               if (!checkReload) {
                  return true;
               }

               var4 = false;
            } finally {
               this.exitNoReload();
            }

            return var4;
         } else {
            return true;
         }
      }
   }

   public void refresh() throws ConfigurationException {
      this.fireEvent(20, (String)null, this.getURL(), true);
      this.setDetailEvents(false);
      boolean autoSaveBak = this.isAutoSave();
      this.setAutoSave(false);

      try {
         this.clear();
         this.load();
      } finally {
         this.setAutoSave(autoSaveBak);
         this.setDetailEvents(true);
      }

      this.fireEvent(20, (String)null, this.getURL(), false);
   }

   public void configurationChanged() {
      this.fireEvent(21, (String)null, this.getURL(), true);
   }

   protected void enterNoReload() {
      synchronized(this.reloadLock) {
         ++this.noReload;
      }
   }

   protected void exitNoReload() {
      synchronized(this.reloadLock) {
         if (this.noReload > 0) {
            --this.noReload;
         }

      }
   }

   protected void fireEvent(int type, String propName, Object propValue, boolean before) {
      this.enterNoReload();

      try {
         super.fireEvent(type, propName, propValue, before);
      } finally {
         this.exitNoReload();
      }

   }

   public Object getProperty(String key) {
      synchronized(this.reloadLock) {
         this.reload();
         return super.getProperty(key);
      }
   }

   public boolean isEmpty() {
      this.reload();
      synchronized(this.reloadLock) {
         return super.isEmpty();
      }
   }

   public boolean containsKey(String key) {
      this.reload();
      synchronized(this.reloadLock) {
         return super.containsKey(key);
      }
   }

   public Iterator getKeys() {
      this.reload();
      List keyList = new LinkedList();
      this.enterNoReload();

      try {
         Iterator it = super.getKeys();

         while(it.hasNext()) {
            keyList.add(it.next());
         }

         it = keyList.iterator();
         return it;
      } finally {
         this.exitNoReload();
      }
   }

   public String getEncoding() {
      return this.encoding;
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public Object clone() {
      AbstractFileConfiguration copy = (AbstractFileConfiguration)super.clone();
      copy.setBasePath((String)null);
      copy.setFileName((String)null);
      copy.initReloadingStrategy();
      return copy;
   }

   private void initReloadingStrategy() {
      this.setReloadingStrategy(new InvariantReloadingStrategy());
   }

   protected void closeSilent(OutputStream out) {
      try {
         if (out != null) {
            out.close();
         }
      } catch (IOException var3) {
         this.getLogger().warn("Could not close output stream", var3);
      }

   }
}
