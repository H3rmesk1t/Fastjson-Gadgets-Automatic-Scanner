package org.apache.commons.configuration;

import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ExpressionEngine;

public class PatternSubtreeConfigurationWrapper extends AbstractHierarchicalFileConfiguration {
   private static ThreadLocal recursive = new ThreadLocal() {
      protected synchronized Boolean initialValue() {
         return Boolean.FALSE;
      }
   };
   private final AbstractHierarchicalFileConfiguration config;
   private final String path;
   private final boolean trailing;
   private boolean init;

   public PatternSubtreeConfigurationWrapper(AbstractHierarchicalFileConfiguration config, String path) {
      this.config = config;
      this.path = path;
      this.trailing = path.endsWith("/");
      this.init = true;
   }

   public Object getReloadLock() {
      return this.config.getReloadLock();
   }

   public void addProperty(String key, Object value) {
      this.config.addProperty(this.makePath(key), value);
   }

   public void clear() {
      this.getConfig().clear();
   }

   public void clearProperty(String key) {
      this.config.clearProperty(this.makePath(key));
   }

   public boolean containsKey(String key) {
      return this.config.containsKey(this.makePath(key));
   }

   public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
      return this.config.getBigDecimal(this.makePath(key), defaultValue);
   }

   public BigDecimal getBigDecimal(String key) {
      return this.config.getBigDecimal(this.makePath(key));
   }

   public BigInteger getBigInteger(String key, BigInteger defaultValue) {
      return this.config.getBigInteger(this.makePath(key), defaultValue);
   }

   public BigInteger getBigInteger(String key) {
      return this.config.getBigInteger(this.makePath(key));
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      return this.config.getBoolean(this.makePath(key), defaultValue);
   }

   public Boolean getBoolean(String key, Boolean defaultValue) {
      return this.config.getBoolean(this.makePath(key), defaultValue);
   }

   public boolean getBoolean(String key) {
      return this.config.getBoolean(this.makePath(key));
   }

   public byte getByte(String key, byte defaultValue) {
      return this.config.getByte(this.makePath(key), defaultValue);
   }

   public Byte getByte(String key, Byte defaultValue) {
      return this.config.getByte(this.makePath(key), defaultValue);
   }

   public byte getByte(String key) {
      return this.config.getByte(this.makePath(key));
   }

   public double getDouble(String key, double defaultValue) {
      return this.config.getDouble(this.makePath(key), defaultValue);
   }

   public Double getDouble(String key, Double defaultValue) {
      return this.config.getDouble(this.makePath(key), defaultValue);
   }

   public double getDouble(String key) {
      return this.config.getDouble(this.makePath(key));
   }

   public float getFloat(String key, float defaultValue) {
      return this.config.getFloat(this.makePath(key), defaultValue);
   }

   public Float getFloat(String key, Float defaultValue) {
      return this.config.getFloat(this.makePath(key), defaultValue);
   }

   public float getFloat(String key) {
      return this.config.getFloat(this.makePath(key));
   }

   public int getInt(String key, int defaultValue) {
      return this.config.getInt(this.makePath(key), defaultValue);
   }

   public int getInt(String key) {
      return this.config.getInt(this.makePath(key));
   }

   public Integer getInteger(String key, Integer defaultValue) {
      return this.config.getInteger(this.makePath(key), defaultValue);
   }

   public Iterator getKeys() {
      return this.config.getKeys(this.makePath());
   }

   public Iterator getKeys(String prefix) {
      return this.config.getKeys(this.makePath(prefix));
   }

   public List getList(String key, List defaultValue) {
      return this.config.getList(this.makePath(key), defaultValue);
   }

   public List getList(String key) {
      return this.config.getList(this.makePath(key));
   }

   public long getLong(String key, long defaultValue) {
      return this.config.getLong(this.makePath(key), defaultValue);
   }

   public Long getLong(String key, Long defaultValue) {
      return this.config.getLong(this.makePath(key), defaultValue);
   }

   public long getLong(String key) {
      return this.config.getLong(this.makePath(key));
   }

   public Properties getProperties(String key) {
      return this.config.getProperties(this.makePath(key));
   }

   public Object getProperty(String key) {
      return this.config.getProperty(this.makePath(key));
   }

   public short getShort(String key, short defaultValue) {
      return this.config.getShort(this.makePath(key), defaultValue);
   }

   public Short getShort(String key, Short defaultValue) {
      return this.config.getShort(this.makePath(key), defaultValue);
   }

   public short getShort(String key) {
      return this.config.getShort(this.makePath(key));
   }

   public String getString(String key, String defaultValue) {
      return this.config.getString(this.makePath(key), defaultValue);
   }

   public String getString(String key) {
      return this.config.getString(this.makePath(key));
   }

   public String[] getStringArray(String key) {
      return this.config.getStringArray(this.makePath(key));
   }

   public boolean isEmpty() {
      return this.getConfig().isEmpty();
   }

   public void setProperty(String key, Object value) {
      this.getConfig().setProperty(key, value);
   }

   public Configuration subset(String prefix) {
      return this.getConfig().subset(prefix);
   }

   public HierarchicalConfiguration.Node getRoot() {
      return this.getConfig().getRoot();
   }

   public void setRoot(HierarchicalConfiguration.Node node) {
      if (this.init) {
         this.getConfig().setRoot(node);
      } else {
         super.setRoot(node);
      }

   }

   public ConfigurationNode getRootNode() {
      return this.getConfig().getRootNode();
   }

   public void setRootNode(ConfigurationNode rootNode) {
      if (this.init) {
         this.getConfig().setRootNode(rootNode);
      } else {
         super.setRootNode(rootNode);
      }

   }

   public ExpressionEngine getExpressionEngine() {
      return this.config.getExpressionEngine();
   }

   public void setExpressionEngine(ExpressionEngine expressionEngine) {
      if (this.init) {
         this.config.setExpressionEngine(expressionEngine);
      } else {
         super.setExpressionEngine(expressionEngine);
      }

   }

   public void addNodes(String key, Collection nodes) {
      this.getConfig().addNodes(key, nodes);
   }

   public SubnodeConfiguration configurationAt(String key, boolean supportUpdates) {
      return this.config.configurationAt(this.makePath(key), supportUpdates);
   }

   public SubnodeConfiguration configurationAt(String key) {
      return this.config.configurationAt(this.makePath(key));
   }

   public List configurationsAt(String key) {
      return this.config.configurationsAt(this.makePath(key));
   }

   public void clearTree(String key) {
      this.config.clearTree(this.makePath(key));
   }

   public int getMaxIndex(String key) {
      return this.config.getMaxIndex(this.makePath(key));
   }

   public Configuration interpolatedConfiguration() {
      return this.getConfig().interpolatedConfiguration();
   }

   public void addConfigurationListener(ConfigurationListener l) {
      this.getConfig().addConfigurationListener(l);
   }

   public boolean removeConfigurationListener(ConfigurationListener l) {
      return this.getConfig().removeConfigurationListener(l);
   }

   public Collection getConfigurationListeners() {
      return this.getConfig().getConfigurationListeners();
   }

   public void clearConfigurationListeners() {
      this.getConfig().clearConfigurationListeners();
   }

   public void addErrorListener(ConfigurationErrorListener l) {
      this.getConfig().addErrorListener(l);
   }

   public boolean removeErrorListener(ConfigurationErrorListener l) {
      return this.getConfig().removeErrorListener(l);
   }

   public void clearErrorListeners() {
      this.getConfig().clearErrorListeners();
   }

   public void save(Writer writer) throws ConfigurationException {
      this.config.save((Writer)writer);
   }

   public void load(Reader reader) throws ConfigurationException {
      this.config.load((Reader)reader);
   }

   public Collection getErrorListeners() {
      return this.getConfig().getErrorListeners();
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

   private HierarchicalConfiguration getConfig() {
      return this.config.configurationAt(this.makePath());
   }

   private String makePath() {
      String pathPattern = this.trailing ? this.path.substring(0, this.path.length() - 1) : this.path;
      return this.getSubstitutor().replace(pathPattern);
   }

   private String makePath(String item) {
      String pathPattern;
      if ((item.length() == 0 || item.startsWith("/")) && this.trailing) {
         pathPattern = this.path.substring(0, this.path.length() - 1);
      } else if (item.startsWith("/") && this.trailing) {
         pathPattern = this.path;
      } else {
         pathPattern = this.path + "/";
      }

      return this.getSubstitutor().replace(pathPattern) + item;
   }
}
