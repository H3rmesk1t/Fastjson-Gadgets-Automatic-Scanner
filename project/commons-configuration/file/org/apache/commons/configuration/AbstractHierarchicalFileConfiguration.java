package org.apache.commons.configuration;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.event.ConfigurationErrorEvent;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.Reloadable;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

public abstract class AbstractHierarchicalFileConfiguration extends HierarchicalConfiguration implements FileConfiguration, ConfigurationListener, ConfigurationErrorListener, FileSystemBased, Reloadable {
   private AbstractHierarchicalFileConfiguration.FileConfigurationDelegate delegate;

   protected AbstractHierarchicalFileConfiguration() {
      this.initialize();
   }

   protected AbstractHierarchicalFileConfiguration(HierarchicalConfiguration c) {
      super(c);
      this.initialize();
   }

   public AbstractHierarchicalFileConfiguration(String fileName) throws ConfigurationException {
      this();
      this.delegate.setFileName(fileName);
      this.load();
   }

   public AbstractHierarchicalFileConfiguration(File file) throws ConfigurationException {
      this();
      this.setFile(file);
      if (file.exists()) {
         this.load();
      }

   }

   public AbstractHierarchicalFileConfiguration(URL url) throws ConfigurationException {
      this();
      this.setURL(url);
      this.load();
   }

   private void initialize() {
      this.delegate = this.createDelegate();
      this.initDelegate(this.delegate);
   }

   protected void addPropertyDirect(String key, Object obj) {
      synchronized(this.delegate.getReloadLock()) {
         super.addPropertyDirect(key, obj);
         this.delegate.possiblySave();
      }
   }

   public void clearProperty(String key) {
      synchronized(this.delegate.getReloadLock()) {
         super.clearProperty(key);
         this.delegate.possiblySave();
      }
   }

   public void clearTree(String key) {
      synchronized(this.delegate.getReloadLock()) {
         super.clearTree(key);
         this.delegate.possiblySave();
      }
   }

   public void setProperty(String key, Object value) {
      synchronized(this.delegate.getReloadLock()) {
         super.setProperty(key, value);
         this.delegate.possiblySave();
      }
   }

   public void load() throws ConfigurationException {
      this.delegate.load();
   }

   public void load(String fileName) throws ConfigurationException {
      this.delegate.load(fileName);
   }

   public void load(File file) throws ConfigurationException {
      this.delegate.load(file);
   }

   public void load(URL url) throws ConfigurationException {
      this.delegate.load(url);
   }

   public void load(InputStream in) throws ConfigurationException {
      this.delegate.load(in);
   }

   public void load(InputStream in, String encoding) throws ConfigurationException {
      this.delegate.load(in, encoding);
   }

   public void save() throws ConfigurationException {
      this.delegate.save();
   }

   public void save(String fileName) throws ConfigurationException {
      this.delegate.save(fileName);
   }

   public void save(File file) throws ConfigurationException {
      this.delegate.save(file);
   }

   public void save(URL url) throws ConfigurationException {
      this.delegate.save(url);
   }

   public void save(OutputStream out) throws ConfigurationException {
      this.delegate.save(out);
   }

   public void save(OutputStream out, String encoding) throws ConfigurationException {
      this.delegate.save(out, encoding);
   }

   public String getFileName() {
      return this.delegate.getFileName();
   }

   public void setFileName(String fileName) {
      this.delegate.setFileName(fileName);
   }

   public String getBasePath() {
      return this.delegate.getBasePath();
   }

   public void setBasePath(String basePath) {
      this.delegate.setBasePath(basePath);
   }

   public File getFile() {
      return this.delegate.getFile();
   }

   public void setFile(File file) {
      this.delegate.setFile(file);
   }

   public URL getURL() {
      return this.delegate.getURL();
   }

   public void setURL(URL url) {
      this.delegate.setURL(url);
   }

   public void setAutoSave(boolean autoSave) {
      this.delegate.setAutoSave(autoSave);
   }

   public boolean isAutoSave() {
      return this.delegate.isAutoSave();
   }

   public ReloadingStrategy getReloadingStrategy() {
      return this.delegate.getReloadingStrategy();
   }

   public void setReloadingStrategy(ReloadingStrategy strategy) {
      this.delegate.setReloadingStrategy(strategy);
   }

   public void reload() {
      this.reload(false);
   }

   private boolean reload(boolean checkReload) {
      synchronized(this.delegate.getReloadLock()) {
         this.setDetailEvents(false);

         boolean var3;
         try {
            var3 = this.delegate.reload(checkReload);
         } finally {
            this.setDetailEvents(true);
         }

         return var3;
      }
   }

   public void refresh() throws ConfigurationException {
      this.delegate.refresh();
   }

   public String getEncoding() {
      return this.delegate.getEncoding();
   }

   public void setEncoding(String encoding) {
      this.delegate.setEncoding(encoding);
   }

   public Object getReloadLock() {
      return this.delegate.getReloadLock();
   }

   public boolean containsKey(String key) {
      this.reload();
      synchronized(this.delegate.getReloadLock()) {
         return super.containsKey(key);
      }
   }

   public Iterator getKeys() {
      this.reload();
      synchronized(this.delegate.getReloadLock()) {
         return super.getKeys();
      }
   }

   public Iterator getKeys(String prefix) {
      this.reload();
      synchronized(this.delegate.getReloadLock()) {
         return super.getKeys(prefix);
      }
   }

   public Object getProperty(String key) {
      if (this.reload(true)) {
         synchronized(this.delegate.getReloadLock()) {
            return super.getProperty(key);
         }
      } else {
         return null;
      }
   }

   public boolean isEmpty() {
      this.reload();
      synchronized(this.delegate.getReloadLock()) {
         return super.isEmpty();
      }
   }

   public void addNodes(String key, Collection nodes) {
      synchronized(this.delegate.getReloadLock()) {
         super.addNodes(key, nodes);
         this.delegate.possiblySave();
      }
   }

   protected List fetchNodeList(String key) {
      this.reload();
      synchronized(this.delegate.getReloadLock()) {
         return super.fetchNodeList(key);
      }
   }

   protected void subnodeConfigurationChanged(ConfigurationEvent event) {
      this.delegate.possiblySave();
      super.subnodeConfigurationChanged(event);
   }

   protected AbstractHierarchicalFileConfiguration.FileConfigurationDelegate createDelegate() {
      return new AbstractHierarchicalFileConfiguration.FileConfigurationDelegate();
   }

   private void initDelegate(AbstractHierarchicalFileConfiguration.FileConfigurationDelegate del) {
      del.addConfigurationListener(this);
      del.addErrorListener(this);
      del.setLogger(this.getLogger());
   }

   public void configurationChanged(ConfigurationEvent event) {
      this.setDetailEvents(true);

      try {
         this.fireEvent(event.getType(), event.getPropertyName(), event.getPropertyValue(), event.isBeforeUpdate());
      } finally {
         this.setDetailEvents(false);
      }

   }

   public void configurationError(ConfigurationErrorEvent event) {
      this.fireError(event.getType(), event.getPropertyName(), event.getPropertyValue(), event.getCause());
   }

   protected AbstractHierarchicalFileConfiguration.FileConfigurationDelegate getDelegate() {
      return this.delegate;
   }

   protected void setDelegate(AbstractHierarchicalFileConfiguration.FileConfigurationDelegate delegate) {
      this.delegate = delegate;
   }

   public void setFileSystem(FileSystem fileSystem) {
      this.delegate.setFileSystem(fileSystem);
   }

   public void resetFileSystem() {
      this.delegate.resetFileSystem();
   }

   public FileSystem getFileSystem() {
      return this.delegate.getFileSystem();
   }

   protected class FileConfigurationDelegate extends AbstractFileConfiguration {
      public void load(Reader in) throws ConfigurationException {
         AbstractHierarchicalFileConfiguration.this.load((Reader)in);
      }

      public void save(Writer out) throws ConfigurationException {
         AbstractHierarchicalFileConfiguration.this.save((Writer)out);
      }

      public void clear() {
         AbstractHierarchicalFileConfiguration.this.clear();
      }
   }
}
