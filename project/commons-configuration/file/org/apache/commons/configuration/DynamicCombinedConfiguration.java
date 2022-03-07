package org.apache.commons.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DynamicCombinedConfiguration extends CombinedConfiguration {
   private static ThreadLocal recursive = new ThreadLocal() {
      protected synchronized Boolean initialValue() {
         return Boolean.FALSE;
      }
   };
   private final ConcurrentMap configs = new ConcurrentHashMap();
   private List configurations = new ArrayList();
   private Map namedConfigurations = new HashMap();
   private String keyPattern;
   private NodeCombiner nodeCombiner;
   private String loggerName = DynamicCombinedConfiguration.class.getName();
   private StrSubstitutor localSubst = new StrSubstitutor(new ConfigurationInterpolator());

   public DynamicCombinedConfiguration(NodeCombiner comb) {
      this.setNodeCombiner(comb);
      this.setIgnoreReloadExceptions(false);
      this.setLogger(LogFactory.getLog(DynamicCombinedConfiguration.class));
   }

   public DynamicCombinedConfiguration() {
      this.setIgnoreReloadExceptions(false);
      this.setLogger(LogFactory.getLog(DynamicCombinedConfiguration.class));
   }

   public void setKeyPattern(String pattern) {
      this.keyPattern = pattern;
   }

   public String getKeyPattern() {
      return this.keyPattern;
   }

   public void setLoggerName(String name) {
      this.loggerName = name;
   }

   public NodeCombiner getNodeCombiner() {
      return this.nodeCombiner;
   }

   public void setNodeCombiner(NodeCombiner nodeCombiner) {
      if (nodeCombiner == null) {
         throw new IllegalArgumentException("Node combiner must not be null!");
      } else {
         this.nodeCombiner = nodeCombiner;
         this.invalidateAll();
      }
   }

   public void addConfiguration(AbstractConfiguration config, String name, String at) {
      DynamicCombinedConfiguration.ConfigData cd = new DynamicCombinedConfiguration.ConfigData(config, name, at);
      this.configurations.add(cd);
      if (name != null) {
         this.namedConfigurations.put(name, config);
      }

   }

   public int getNumberOfConfigurations() {
      return this.configurations.size();
   }

   public Configuration getConfiguration(int index) {
      DynamicCombinedConfiguration.ConfigData cd = (DynamicCombinedConfiguration.ConfigData)this.configurations.get(index);
      return cd.getConfiguration();
   }

   public Configuration getConfiguration(String name) {
      return (Configuration)this.namedConfigurations.get(name);
   }

   public Set getConfigurationNames() {
      return this.namedConfigurations.keySet();
   }

   public Configuration removeConfiguration(String name) {
      Configuration conf = this.getConfiguration(name);
      if (conf != null) {
         this.removeConfiguration(conf);
      }

      return conf;
   }

   public boolean removeConfiguration(Configuration config) {
      for(int index = 0; index < this.getNumberOfConfigurations(); ++index) {
         if (((DynamicCombinedConfiguration.ConfigData)this.configurations.get(index)).getConfiguration() == config) {
            this.removeConfigurationAt(index);
         }
      }

      return super.removeConfiguration(config);
   }

   public Configuration removeConfigurationAt(int index) {
      DynamicCombinedConfiguration.ConfigData cd = (DynamicCombinedConfiguration.ConfigData)this.configurations.remove(index);
      if (cd.getName() != null) {
         this.namedConfigurations.remove(cd.getName());
      }

      return super.removeConfigurationAt(index);
   }

   public ConfigurationNode getRootNode() {
      return this.getCurrentConfig().getRootNode();
   }

   public void setRootNode(ConfigurationNode rootNode) {
      if (this.configs != null) {
         this.getCurrentConfig().setRootNode(rootNode);
      } else {
         super.setRootNode(rootNode);
      }

   }

   public void addProperty(String key, Object value) {
      this.getCurrentConfig().addProperty(key, value);
   }

   public void clear() {
      if (this.configs != null) {
         this.getCurrentConfig().clear();
      }

   }

   public void clearProperty(String key) {
      this.getCurrentConfig().clearProperty(key);
   }

   public boolean containsKey(String key) {
      return this.getCurrentConfig().containsKey(key);
   }

   public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
      return this.getCurrentConfig().getBigDecimal(key, defaultValue);
   }

   public BigDecimal getBigDecimal(String key) {
      return this.getCurrentConfig().getBigDecimal(key);
   }

   public BigInteger getBigInteger(String key, BigInteger defaultValue) {
      return this.getCurrentConfig().getBigInteger(key, defaultValue);
   }

   public BigInteger getBigInteger(String key) {
      return this.getCurrentConfig().getBigInteger(key);
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      return this.getCurrentConfig().getBoolean(key, defaultValue);
   }

   public Boolean getBoolean(String key, Boolean defaultValue) {
      return this.getCurrentConfig().getBoolean(key, defaultValue);
   }

   public boolean getBoolean(String key) {
      return this.getCurrentConfig().getBoolean(key);
   }

   public byte getByte(String key, byte defaultValue) {
      return this.getCurrentConfig().getByte(key, defaultValue);
   }

   public Byte getByte(String key, Byte defaultValue) {
      return this.getCurrentConfig().getByte(key, defaultValue);
   }

   public byte getByte(String key) {
      return this.getCurrentConfig().getByte(key);
   }

   public double getDouble(String key, double defaultValue) {
      return this.getCurrentConfig().getDouble(key, defaultValue);
   }

   public Double getDouble(String key, Double defaultValue) {
      return this.getCurrentConfig().getDouble(key, defaultValue);
   }

   public double getDouble(String key) {
      return this.getCurrentConfig().getDouble(key);
   }

   public float getFloat(String key, float defaultValue) {
      return this.getCurrentConfig().getFloat(key, defaultValue);
   }

   public Float getFloat(String key, Float defaultValue) {
      return this.getCurrentConfig().getFloat(key, defaultValue);
   }

   public float getFloat(String key) {
      return this.getCurrentConfig().getFloat(key);
   }

   public int getInt(String key, int defaultValue) {
      return this.getCurrentConfig().getInt(key, defaultValue);
   }

   public int getInt(String key) {
      return this.getCurrentConfig().getInt(key);
   }

   public Integer getInteger(String key, Integer defaultValue) {
      return this.getCurrentConfig().getInteger(key, defaultValue);
   }

   public Iterator getKeys() {
      return this.getCurrentConfig().getKeys();
   }

   public Iterator getKeys(String prefix) {
      return this.getCurrentConfig().getKeys(prefix);
   }

   public List getList(String key, List defaultValue) {
      return this.getCurrentConfig().getList(key, defaultValue);
   }

   public List getList(String key) {
      return this.getCurrentConfig().getList(key);
   }

   public long getLong(String key, long defaultValue) {
      return this.getCurrentConfig().getLong(key, defaultValue);
   }

   public Long getLong(String key, Long defaultValue) {
      return this.getCurrentConfig().getLong(key, defaultValue);
   }

   public long getLong(String key) {
      return this.getCurrentConfig().getLong(key);
   }

   public Properties getProperties(String key) {
      return this.getCurrentConfig().getProperties(key);
   }

   public Object getProperty(String key) {
      return this.getCurrentConfig().getProperty(key);
   }

   public short getShort(String key, short defaultValue) {
      return this.getCurrentConfig().getShort(key, defaultValue);
   }

   public Short getShort(String key, Short defaultValue) {
      return this.getCurrentConfig().getShort(key, defaultValue);
   }

   public short getShort(String key) {
      return this.getCurrentConfig().getShort(key);
   }

   public String getString(String key, String defaultValue) {
      return this.getCurrentConfig().getString(key, defaultValue);
   }

   public String getString(String key) {
      return this.getCurrentConfig().getString(key);
   }

   public String[] getStringArray(String key) {
      return this.getCurrentConfig().getStringArray(key);
   }

   public boolean isEmpty() {
      return this.getCurrentConfig().isEmpty();
   }

   public void setProperty(String key, Object value) {
      if (this.configs != null) {
         this.getCurrentConfig().setProperty(key, value);
      }

   }

   public Configuration subset(String prefix) {
      return this.getCurrentConfig().subset(prefix);
   }

   public HierarchicalConfiguration.Node getRoot() {
      return this.getCurrentConfig().getRoot();
   }

   public void setRoot(HierarchicalConfiguration.Node node) {
      if (this.configs != null) {
         this.getCurrentConfig().setRoot(node);
      } else {
         super.setRoot(node);
      }

   }

   public ExpressionEngine getExpressionEngine() {
      return super.getExpressionEngine();
   }

   public void setExpressionEngine(ExpressionEngine expressionEngine) {
      super.setExpressionEngine(expressionEngine);
   }

   public void addNodes(String key, Collection nodes) {
      this.getCurrentConfig().addNodes(key, nodes);
   }

   public SubnodeConfiguration configurationAt(String key, boolean supportUpdates) {
      return this.getCurrentConfig().configurationAt(key, supportUpdates);
   }

   public SubnodeConfiguration configurationAt(String key) {
      return this.getCurrentConfig().configurationAt(key);
   }

   public List configurationsAt(String key) {
      return this.getCurrentConfig().configurationsAt(key);
   }

   public void clearTree(String key) {
      this.getCurrentConfig().clearTree(key);
   }

   public int getMaxIndex(String key) {
      return this.getCurrentConfig().getMaxIndex(key);
   }

   public Configuration interpolatedConfiguration() {
      return this.getCurrentConfig().interpolatedConfiguration();
   }

   public Configuration getSource(String key) {
      if (key == null) {
         throw new IllegalArgumentException("Key must not be null!");
      } else {
         return this.getCurrentConfig().getSource(key);
      }
   }

   public void addConfigurationListener(ConfigurationListener l) {
      super.addConfigurationListener(l);
      Iterator i$ = this.configs.values().iterator();

      while(i$.hasNext()) {
         CombinedConfiguration cc = (CombinedConfiguration)i$.next();
         cc.addConfigurationListener(l);
      }

   }

   public boolean removeConfigurationListener(ConfigurationListener l) {
      Iterator i$ = this.configs.values().iterator();

      while(i$.hasNext()) {
         CombinedConfiguration cc = (CombinedConfiguration)i$.next();
         cc.removeConfigurationListener(l);
      }

      return super.removeConfigurationListener(l);
   }

   public Collection getConfigurationListeners() {
      return super.getConfigurationListeners();
   }

   public void clearConfigurationListeners() {
      Iterator i$ = this.configs.values().iterator();

      while(i$.hasNext()) {
         CombinedConfiguration cc = (CombinedConfiguration)i$.next();
         cc.clearConfigurationListeners();
      }

      super.clearConfigurationListeners();
   }

   public void addErrorListener(ConfigurationErrorListener l) {
      Iterator i$ = this.configs.values().iterator();

      while(i$.hasNext()) {
         CombinedConfiguration cc = (CombinedConfiguration)i$.next();
         cc.addErrorListener(l);
      }

      super.addErrorListener(l);
   }

   public boolean removeErrorListener(ConfigurationErrorListener l) {
      Iterator i$ = this.configs.values().iterator();

      while(i$.hasNext()) {
         CombinedConfiguration cc = (CombinedConfiguration)i$.next();
         cc.removeErrorListener(l);
      }

      return super.removeErrorListener(l);
   }

   public void clearErrorListeners() {
      Iterator i$ = this.configs.values().iterator();

      while(i$.hasNext()) {
         CombinedConfiguration cc = (CombinedConfiguration)i$.next();
         cc.clearErrorListeners();
      }

      super.clearErrorListeners();
   }

   public Collection getErrorListeners() {
      return super.getErrorListeners();
   }

   public Object clone() {
      return super.clone();
   }

   public void invalidate() {
      this.getCurrentConfig().invalidate();
   }

   public void invalidateAll() {
      if (this.configs != null) {
         Iterator i$ = this.configs.values().iterator();

         while(i$.hasNext()) {
            CombinedConfiguration cc = (CombinedConfiguration)i$.next();
            cc.invalidate();
         }

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

   private CombinedConfiguration getCurrentConfig() {
      String key = this.localSubst.replace(this.keyPattern);
      CombinedConfiguration config = (CombinedConfiguration)this.configs.get(key);
      if (config == null) {
         synchronized(this.configs) {
            config = (CombinedConfiguration)this.configs.get(key);
            if (config == null) {
               config = new CombinedConfiguration(this.getNodeCombiner());
               if (this.loggerName != null) {
                  Log log = LogFactory.getLog(this.loggerName);
                  if (log != null) {
                     config.setLogger(log);
                  }
               }

               config.setIgnoreReloadExceptions(this.isIgnoreReloadExceptions());
               config.setExpressionEngine(this.getExpressionEngine());
               config.setDelimiterParsingDisabled(this.isDelimiterParsingDisabled());
               config.setConversionExpressionEngine(this.getConversionExpressionEngine());
               config.setListDelimiter(this.getListDelimiter());
               Iterator i$ = this.getErrorListeners().iterator();

               while(i$.hasNext()) {
                  ConfigurationErrorListener listener = (ConfigurationErrorListener)i$.next();
                  config.addErrorListener(listener);
               }

               i$ = this.getConfigurationListeners().iterator();

               while(i$.hasNext()) {
                  ConfigurationListener listener = (ConfigurationListener)i$.next();
                  config.addConfigurationListener(listener);
               }

               config.setForceReloadCheck(this.isForceReloadCheck());
               i$ = this.configurations.iterator();

               while(i$.hasNext()) {
                  DynamicCombinedConfiguration.ConfigData data = (DynamicCombinedConfiguration.ConfigData)i$.next();
                  config.addConfiguration(data.getConfiguration(), data.getName(), data.getAt());
               }

               this.configs.put(key, config);
            }
         }
      }

      if (this.getLogger().isDebugEnabled()) {
         this.getLogger().debug("Returning config for " + key + ": " + config);
      }

      return config;
   }

   static class ConfigData {
      private AbstractConfiguration configuration;
      private String name;
      private String at;

      public ConfigData(AbstractConfiguration config, String n, String at) {
         this.configuration = config;
         this.name = n;
         this.at = at;
      }

      public AbstractConfiguration getConfiguration() {
         return this.configuration;
      }

      public String getName() {
         return this.name;
      }

      public String getAt() {
         return this.at;
      }
   }
}
