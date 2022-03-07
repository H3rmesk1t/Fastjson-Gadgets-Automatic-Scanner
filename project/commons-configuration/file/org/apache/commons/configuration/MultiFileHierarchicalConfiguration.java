package org.apache.commons.configuration;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.configuration.event.ConfigurationErrorEvent;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.apache.commons.configuration.resolver.EntityResolverSupport;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXParseException;

public class MultiFileHierarchicalConfiguration extends AbstractHierarchicalFileConfiguration implements ConfigurationListener, ConfigurationErrorListener, EntityResolverSupport {
   private static ThreadLocal recursive = new ThreadLocal() {
      protected synchronized Boolean initialValue() {
         return Boolean.FALSE;
      }
   };
   private final ConcurrentMap configurationsMap = new ConcurrentHashMap();
   private String pattern;
   private boolean init;
   private boolean ignoreException = true;
   private boolean schemaValidation;
   private boolean validating;
   private boolean attributeSplittingDisabled;
   private String loggerName = MultiFileHierarchicalConfiguration.class.getName();
   private ReloadingStrategy fileStrategy;
   private EntityResolver entityResolver;
   private StrSubstitutor localSubst = new StrSubstitutor(new ConfigurationInterpolator());

   public MultiFileHierarchicalConfiguration() {
      this.init = true;
      this.setLogger(LogFactory.getLog(this.loggerName));
   }

   public MultiFileHierarchicalConfiguration(String pathPattern) {
      this.pattern = pathPattern;
      this.init = true;
      this.setLogger(LogFactory.getLog(this.loggerName));
   }

   public void setLoggerName(String name) {
      this.loggerName = name;
   }

   public void setFilePattern(String pathPattern) {
      this.pattern = pathPattern;
   }

   public boolean isSchemaValidation() {
      return this.schemaValidation;
   }

   public void setSchemaValidation(boolean schemaValidation) {
      this.schemaValidation = schemaValidation;
   }

   public boolean isValidating() {
      return this.validating;
   }

   public void setValidating(boolean validating) {
      this.validating = validating;
   }

   public boolean isAttributeSplittingDisabled() {
      return this.attributeSplittingDisabled;
   }

   public void setAttributeSplittingDisabled(boolean attributeSplittingDisabled) {
      this.attributeSplittingDisabled = attributeSplittingDisabled;
   }

   public ReloadingStrategy getReloadingStrategy() {
      return this.fileStrategy;
   }

   public void setReloadingStrategy(ReloadingStrategy strategy) {
      this.fileStrategy = strategy;
   }

   public void setEntityResolver(EntityResolver entityResolver) {
      this.entityResolver = entityResolver;
   }

   public EntityResolver getEntityResolver() {
      return this.entityResolver;
   }

   public void setIgnoreException(boolean ignoreException) {
      this.ignoreException = ignoreException;
   }

   public void addProperty(String key, Object value) {
      this.getConfiguration().addProperty(key, value);
   }

   public void clear() {
      this.getConfiguration().clear();
   }

   public void clearProperty(String key) {
      this.getConfiguration().clearProperty(key);
   }

   public boolean containsKey(String key) {
      return this.getConfiguration().containsKey(key);
   }

   public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
      return this.getConfiguration().getBigDecimal(key, defaultValue);
   }

   public BigDecimal getBigDecimal(String key) {
      return this.getConfiguration().getBigDecimal(key);
   }

   public BigInteger getBigInteger(String key, BigInteger defaultValue) {
      return this.getConfiguration().getBigInteger(key, defaultValue);
   }

   public BigInteger getBigInteger(String key) {
      return this.getConfiguration().getBigInteger(key);
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      return this.getConfiguration().getBoolean(key, defaultValue);
   }

   public Boolean getBoolean(String key, Boolean defaultValue) {
      return this.getConfiguration().getBoolean(key, defaultValue);
   }

   public boolean getBoolean(String key) {
      return this.getConfiguration().getBoolean(key);
   }

   public byte getByte(String key, byte defaultValue) {
      return this.getConfiguration().getByte(key, defaultValue);
   }

   public Byte getByte(String key, Byte defaultValue) {
      return this.getConfiguration().getByte(key, defaultValue);
   }

   public byte getByte(String key) {
      return this.getConfiguration().getByte(key);
   }

   public double getDouble(String key, double defaultValue) {
      return this.getConfiguration().getDouble(key, defaultValue);
   }

   public Double getDouble(String key, Double defaultValue) {
      return this.getConfiguration().getDouble(key, defaultValue);
   }

   public double getDouble(String key) {
      return this.getConfiguration().getDouble(key);
   }

   public float getFloat(String key, float defaultValue) {
      return this.getConfiguration().getFloat(key, defaultValue);
   }

   public Float getFloat(String key, Float defaultValue) {
      return this.getConfiguration().getFloat(key, defaultValue);
   }

   public float getFloat(String key) {
      return this.getConfiguration().getFloat(key);
   }

   public int getInt(String key, int defaultValue) {
      return this.getConfiguration().getInt(key, defaultValue);
   }

   public int getInt(String key) {
      return this.getConfiguration().getInt(key);
   }

   public Integer getInteger(String key, Integer defaultValue) {
      return this.getConfiguration().getInteger(key, defaultValue);
   }

   public Iterator getKeys() {
      return this.getConfiguration().getKeys();
   }

   public Iterator getKeys(String prefix) {
      return this.getConfiguration().getKeys(prefix);
   }

   public List getList(String key, List defaultValue) {
      return this.getConfiguration().getList(key, defaultValue);
   }

   public List getList(String key) {
      return this.getConfiguration().getList(key);
   }

   public long getLong(String key, long defaultValue) {
      return this.getConfiguration().getLong(key, defaultValue);
   }

   public Long getLong(String key, Long defaultValue) {
      return this.getConfiguration().getLong(key, defaultValue);
   }

   public long getLong(String key) {
      return this.getConfiguration().getLong(key);
   }

   public Properties getProperties(String key) {
      return this.getConfiguration().getProperties(key);
   }

   public Object getProperty(String key) {
      return this.getConfiguration().getProperty(key);
   }

   public short getShort(String key, short defaultValue) {
      return this.getConfiguration().getShort(key, defaultValue);
   }

   public Short getShort(String key, Short defaultValue) {
      return this.getConfiguration().getShort(key, defaultValue);
   }

   public short getShort(String key) {
      return this.getConfiguration().getShort(key);
   }

   public String getString(String key, String defaultValue) {
      return this.getConfiguration().getString(key, defaultValue);
   }

   public String getString(String key) {
      return this.getConfiguration().getString(key);
   }

   public String[] getStringArray(String key) {
      return this.getConfiguration().getStringArray(key);
   }

   public boolean isEmpty() {
      return this.getConfiguration().isEmpty();
   }

   public void setProperty(String key, Object value) {
      if (this.init) {
         this.getConfiguration().setProperty(key, value);
      }

   }

   public Configuration subset(String prefix) {
      return this.getConfiguration().subset(prefix);
   }

   public Object getReloadLock() {
      return this.getConfiguration().getReloadLock();
   }

   public HierarchicalConfiguration.Node getRoot() {
      return this.getConfiguration().getRoot();
   }

   public void setRoot(HierarchicalConfiguration.Node node) {
      if (this.init) {
         this.getConfiguration().setRoot(node);
      } else {
         super.setRoot(node);
      }

   }

   public ConfigurationNode getRootNode() {
      return this.getConfiguration().getRootNode();
   }

   public void setRootNode(ConfigurationNode rootNode) {
      if (this.init) {
         this.getConfiguration().setRootNode(rootNode);
      } else {
         super.setRootNode(rootNode);
      }

   }

   public ExpressionEngine getExpressionEngine() {
      return super.getExpressionEngine();
   }

   public void setExpressionEngine(ExpressionEngine expressionEngine) {
      super.setExpressionEngine(expressionEngine);
   }

   public void addNodes(String key, Collection nodes) {
      this.getConfiguration().addNodes(key, nodes);
   }

   public SubnodeConfiguration configurationAt(String key, boolean supportUpdates) {
      return this.getConfiguration().configurationAt(key, supportUpdates);
   }

   public SubnodeConfiguration configurationAt(String key) {
      return this.getConfiguration().configurationAt(key);
   }

   public List configurationsAt(String key) {
      return this.getConfiguration().configurationsAt(key);
   }

   public void clearTree(String key) {
      this.getConfiguration().clearTree(key);
   }

   public int getMaxIndex(String key) {
      return this.getConfiguration().getMaxIndex(key);
   }

   public Configuration interpolatedConfiguration() {
      return this.getConfiguration().interpolatedConfiguration();
   }

   public void addConfigurationListener(ConfigurationListener l) {
      super.addConfigurationListener(l);
   }

   public boolean removeConfigurationListener(ConfigurationListener l) {
      return super.removeConfigurationListener(l);
   }

   public Collection getConfigurationListeners() {
      return super.getConfigurationListeners();
   }

   public void clearConfigurationListeners() {
      super.clearConfigurationListeners();
   }

   public void addErrorListener(ConfigurationErrorListener l) {
      super.addErrorListener(l);
   }

   public boolean removeErrorListener(ConfigurationErrorListener l) {
      return super.removeErrorListener(l);
   }

   public void clearErrorListeners() {
      super.clearErrorListeners();
   }

   public Collection getErrorListeners() {
      return super.getErrorListeners();
   }

   public void save(Writer writer) throws ConfigurationException {
      if (this.init) {
         this.getConfiguration().save((Writer)writer);
      }

   }

   public void load(Reader reader) throws ConfigurationException {
      if (this.init) {
         this.getConfiguration().load((Reader)reader);
      }

   }

   public void load() throws ConfigurationException {
      this.getConfiguration();
   }

   public void load(String fileName) throws ConfigurationException {
      this.getConfiguration().load(fileName);
   }

   public void load(File file) throws ConfigurationException {
      this.getConfiguration().load(file);
   }

   public void load(URL url) throws ConfigurationException {
      this.getConfiguration().load(url);
   }

   public void load(InputStream in) throws ConfigurationException {
      this.getConfiguration().load(in);
   }

   public void load(InputStream in, String encoding) throws ConfigurationException {
      this.getConfiguration().load(in, encoding);
   }

   public void save() throws ConfigurationException {
      this.getConfiguration().save();
   }

   public void save(String fileName) throws ConfigurationException {
      this.getConfiguration().save(fileName);
   }

   public void save(File file) throws ConfigurationException {
      this.getConfiguration().save(file);
   }

   public void save(URL url) throws ConfigurationException {
      this.getConfiguration().save(url);
   }

   public void save(OutputStream out) throws ConfigurationException {
      this.getConfiguration().save(out);
   }

   public void save(OutputStream out, String encoding) throws ConfigurationException {
      this.getConfiguration().save(out, encoding);
   }

   public void configurationChanged(ConfigurationEvent event) {
      if (event.getSource() instanceof XMLConfiguration) {
         Iterator i$ = this.getConfigurationListeners().iterator();

         while(i$.hasNext()) {
            ConfigurationListener listener = (ConfigurationListener)i$.next();
            listener.configurationChanged(event);
         }
      }

   }

   public void configurationError(ConfigurationErrorEvent event) {
      if (event.getSource() instanceof XMLConfiguration) {
         Iterator i$ = this.getErrorListeners().iterator();

         while(i$.hasNext()) {
            ConfigurationErrorListener listener = (ConfigurationErrorListener)i$.next();
            listener.configurationError(event);
         }
      }

      if (event.getType() == 20 && this.isThrowable(event.getCause())) {
         throw new ConfigurationRuntimeException(event.getCause());
      }
   }

   protected Object resolveContainerStore(String key) {
      if ((Boolean)recursive.get()) {
         return null;
      } else {
         recursive.set(Boolean.TRUE);

         Object var2;
         try {
            var2 = super.resolveContainerStore(key);
         } finally {
            recursive.set(Boolean.FALSE);
         }

         return var2;
      }
   }

   public void removeConfiguration() {
      String path = this.getSubstitutor().replace(this.pattern);
      this.configurationsMap.remove(path);
   }

   private AbstractHierarchicalFileConfiguration getConfiguration() {
      if (this.pattern == null) {
         throw new ConfigurationRuntimeException("File pattern must be defined");
      } else {
         String path = this.localSubst.replace(this.pattern);
         if (this.configurationsMap.containsKey(path)) {
            return (AbstractHierarchicalFileConfiguration)this.configurationsMap.get(path);
         } else {
            XMLConfiguration configuration;
            if (path.equals(this.pattern)) {
               configuration = new XMLConfiguration() {
                  public void load() throws ConfigurationException {
                  }

                  public void save() throws ConfigurationException {
                  }
               };
               this.configurationsMap.putIfAbsent(this.pattern, configuration);
               return configuration;
            } else {
               configuration = new XMLConfiguration();
               if (this.loggerName != null) {
                  Log log = LogFactory.getLog(this.loggerName);
                  if (log != null) {
                     configuration.setLogger(log);
                  }
               }

               configuration.setBasePath(this.getBasePath());
               configuration.setFileName(path);
               configuration.setFileSystem(this.getFileSystem());
               configuration.setExpressionEngine(this.getExpressionEngine());
               ReloadingStrategy strategy = this.createReloadingStrategy();
               if (strategy != null) {
                  configuration.setReloadingStrategy(strategy);
               }

               configuration.setDelimiterParsingDisabled(this.isDelimiterParsingDisabled());
               configuration.setAttributeSplittingDisabled(this.isAttributeSplittingDisabled());
               configuration.setValidating(this.validating);
               configuration.setSchemaValidation(this.schemaValidation);
               configuration.setEntityResolver(this.entityResolver);
               configuration.setListDelimiter(this.getListDelimiter());
               configuration.addConfigurationListener(this);
               configuration.addErrorListener(this);

               try {
                  configuration.load();
               } catch (ConfigurationException var5) {
                  if (this.isThrowable(var5)) {
                     throw new ConfigurationRuntimeException(var5);
                  }
               }

               this.configurationsMap.putIfAbsent(path, configuration);
               return (AbstractHierarchicalFileConfiguration)this.configurationsMap.get(path);
            }
         }
      }
   }

   private boolean isThrowable(Throwable throwable) {
      if (!this.ignoreException) {
         return true;
      } else {
         Throwable cause;
         for(cause = throwable.getCause(); cause != null && !(cause instanceof SAXParseException); cause = cause.getCause()) {
         }

         return cause != null;
      }
   }

   private ReloadingStrategy createReloadingStrategy() {
      if (this.fileStrategy == null) {
         return null;
      } else {
         try {
            ReloadingStrategy strategy = (ReloadingStrategy)BeanUtils.cloneBean(this.fileStrategy);
            strategy.setConfiguration((FileConfiguration)null);
            return strategy;
         } catch (Exception var2) {
            return null;
         }
      }
   }
}
