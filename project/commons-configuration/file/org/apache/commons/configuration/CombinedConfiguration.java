package org.apache.commons.configuration;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationKey;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.configuration.tree.TreeUtils;
import org.apache.commons.configuration.tree.UnionCombiner;
import org.apache.commons.configuration.tree.ViewNode;

public class CombinedConfiguration extends HierarchicalReloadableConfiguration implements ConfigurationListener, Cloneable {
   public static final int EVENT_COMBINED_INVALIDATE = 40;
   private static final long serialVersionUID = 8338574525528692307L;
   private static final DefaultExpressionEngine AT_ENGINE = new DefaultExpressionEngine();
   private static final NodeCombiner DEFAULT_COMBINER = new UnionCombiner();
   private static final String PROP_RELOAD_CHECK = "CombinedConfigurationReloadCheck";
   private NodeCombiner nodeCombiner;
   private volatile ConfigurationNode combinedRoot;
   private List configurations;
   private Map namedConfigurations;
   private boolean ignoreReloadExceptions;
   private boolean reloadRequired;
   private ExpressionEngine conversionExpressionEngine;
   private boolean forceReloadCheck;

   public CombinedConfiguration(NodeCombiner comb) {
      this.ignoreReloadExceptions = true;
      this.setNodeCombiner(comb != null ? comb : DEFAULT_COMBINER);
      this.clear();
   }

   public CombinedConfiguration(NodeCombiner comb, Lock lock) {
      super((Object)lock);
      this.ignoreReloadExceptions = true;
      this.setNodeCombiner(comb != null ? comb : DEFAULT_COMBINER);
      this.clear();
   }

   public CombinedConfiguration(Lock lock) {
      this((NodeCombiner)null, lock);
   }

   public CombinedConfiguration() {
      this((NodeCombiner)null, (Lock)null);
   }

   public NodeCombiner getNodeCombiner() {
      return this.nodeCombiner;
   }

   public void setNodeCombiner(NodeCombiner nodeCombiner) {
      if (nodeCombiner == null) {
         throw new IllegalArgumentException("Node combiner must not be null!");
      } else {
         this.nodeCombiner = nodeCombiner;
         this.invalidate();
      }
   }

   public boolean isForceReloadCheck() {
      return this.forceReloadCheck;
   }

   public void setForceReloadCheck(boolean forceReloadCheck) {
      this.forceReloadCheck = forceReloadCheck;
   }

   public ExpressionEngine getConversionExpressionEngine() {
      return this.conversionExpressionEngine;
   }

   public void setConversionExpressionEngine(ExpressionEngine conversionExpressionEngine) {
      this.conversionExpressionEngine = conversionExpressionEngine;
   }

   public boolean isIgnoreReloadExceptions() {
      return this.ignoreReloadExceptions;
   }

   public void setIgnoreReloadExceptions(boolean ignoreReloadExceptions) {
      this.ignoreReloadExceptions = ignoreReloadExceptions;
   }

   public void addConfiguration(AbstractConfiguration config, String name, String at) {
      if (config == null) {
         throw new IllegalArgumentException("Added configuration must not be null!");
      } else if (name != null && this.namedConfigurations.containsKey(name)) {
         throw new ConfigurationRuntimeException("A configuration with the name '" + name + "' already exists in this combined configuration!");
      } else {
         CombinedConfiguration.ConfigData cd = new CombinedConfiguration.ConfigData(config, name, at);
         if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("Adding configuration " + config + " with name " + name);
         }

         this.configurations.add(cd);
         if (name != null) {
            this.namedConfigurations.put(name, config);
         }

         config.addConfigurationListener(this);
         this.invalidate();
      }
   }

   public void addConfiguration(AbstractConfiguration config, String name) {
      this.addConfiguration(config, name, (String)null);
   }

   public void addConfiguration(AbstractConfiguration config) {
      this.addConfiguration(config, (String)null, (String)null);
   }

   public int getNumberOfConfigurations() {
      return this.configurations.size();
   }

   public Configuration getConfiguration(int index) {
      CombinedConfiguration.ConfigData cd = (CombinedConfiguration.ConfigData)this.configurations.get(index);
      return cd.getConfiguration();
   }

   public Configuration getConfiguration(String name) {
      return (Configuration)this.namedConfigurations.get(name);
   }

   public List getConfigurations() {
      List list = new ArrayList(this.configurations.size());
      Iterator i$ = this.configurations.iterator();

      while(i$.hasNext()) {
         CombinedConfiguration.ConfigData cd = (CombinedConfiguration.ConfigData)i$.next();
         list.add(cd.getConfiguration());
      }

      return list;
   }

   public List getConfigurationNameList() {
      List list = new ArrayList(this.configurations.size());
      Iterator i$ = this.configurations.iterator();

      while(i$.hasNext()) {
         CombinedConfiguration.ConfigData cd = (CombinedConfiguration.ConfigData)i$.next();
         list.add(cd.getName());
      }

      return list;
   }

   public boolean removeConfiguration(Configuration config) {
      for(int index = 0; index < this.getNumberOfConfigurations(); ++index) {
         if (((CombinedConfiguration.ConfigData)this.configurations.get(index)).getConfiguration() == config) {
            this.removeConfigurationAt(index);
            return true;
         }
      }

      return false;
   }

   public Configuration removeConfigurationAt(int index) {
      CombinedConfiguration.ConfigData cd = (CombinedConfiguration.ConfigData)this.configurations.remove(index);
      if (cd.getName() != null) {
         this.namedConfigurations.remove(cd.getName());
      }

      cd.getConfiguration().removeConfigurationListener(this);
      this.invalidate();
      return cd.getConfiguration();
   }

   public Configuration removeConfiguration(String name) {
      Configuration conf = this.getConfiguration(name);
      if (conf != null) {
         this.removeConfiguration(conf);
      }

      return conf;
   }

   public Set getConfigurationNames() {
      return this.namedConfigurations.keySet();
   }

   public void invalidate() {
      this.reloadRequired = true;
      this.fireEvent(40, (String)null, (Object)null, false);
   }

   public void configurationChanged(ConfigurationEvent event) {
      if (event.getType() == 21) {
         this.fireEvent(event.getType(), event.getPropertyName(), event.getPropertyValue(), event.isBeforeUpdate());
      } else if (!event.isBeforeUpdate()) {
         this.invalidate();
      }

   }

   public ConfigurationNode getRootNode() {
      synchronized(this.getReloadLock()) {
         if (this.reloadRequired || this.combinedRoot == null) {
            this.combinedRoot = this.constructCombinedNode();
            this.reloadRequired = false;
         }

         return this.combinedRoot;
      }
   }

   public void clear() {
      this.fireEvent(4, (String)null, (Object)null, true);
      this.configurations = new ArrayList();
      this.namedConfigurations = new HashMap();
      this.fireEvent(4, (String)null, (Object)null, false);
      this.invalidate();
   }

   public Object clone() {
      CombinedConfiguration copy = (CombinedConfiguration)super.clone();
      copy.clear();
      Iterator i$ = this.configurations.iterator();

      while(i$.hasNext()) {
         CombinedConfiguration.ConfigData cd = (CombinedConfiguration.ConfigData)i$.next();
         copy.addConfiguration((AbstractConfiguration)ConfigurationUtils.cloneConfiguration(cd.getConfiguration()), cd.getName(), cd.getAt());
      }

      copy.setRootNode(new DefaultConfigurationNode());
      return copy;
   }

   public Configuration getSource(String key) {
      if (key == null) {
         throw new IllegalArgumentException("Key must not be null!");
      } else {
         List nodes = this.fetchNodeList(key);
         if (nodes.isEmpty()) {
            return null;
         } else {
            Iterator it = nodes.iterator();
            Configuration source = this.findSourceConfiguration((ConfigurationNode)it.next());

            Configuration src;
            do {
               if (!it.hasNext()) {
                  return source;
               }

               src = this.findSourceConfiguration((ConfigurationNode)it.next());
            } while(src == source);

            throw new IllegalArgumentException("The key " + key + " is defined by multiple sources!");
         }
      }
   }

   protected List fetchNodeList(String key) {
      if (this.isForceReloadCheck()) {
         this.performReloadCheck();
      }

      return super.fetchNodeList(key);
   }

   protected void performReloadCheck() {
      Iterator i$ = this.configurations.iterator();

      while(i$.hasNext()) {
         CombinedConfiguration.ConfigData cd = (CombinedConfiguration.ConfigData)i$.next();

         try {
            cd.getConfiguration().getProperty("CombinedConfigurationReloadCheck");
         } catch (Exception var4) {
            if (!this.ignoreReloadExceptions) {
               throw new ConfigurationRuntimeException(var4);
            }
         }
      }

   }

   private ConfigurationNode constructCombinedNode() {
      if (this.getNumberOfConfigurations() < 1) {
         if (this.getLogger().isDebugEnabled()) {
            this.getLogger().debug("No configurations defined for " + this);
         }

         return new ViewNode();
      } else {
         Iterator it = this.configurations.iterator();

         ConfigurationNode node;
         for(node = ((CombinedConfiguration.ConfigData)it.next()).getTransformedRoot(); it.hasNext(); node = this.getNodeCombiner().combine(node, ((CombinedConfiguration.ConfigData)it.next()).getTransformedRoot())) {
         }

         if (this.getLogger().isDebugEnabled()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(os);
            TreeUtils.printTree(stream, node);
            this.getLogger().debug(os.toString());
         }

         return node;
      }
   }

   private Configuration findSourceConfiguration(ConfigurationNode node) {
      synchronized(this.getReloadLock()) {
         ConfigurationNode root = null;

         for(ConfigurationNode current = node; current != null; current = current.getParentNode()) {
            root = current;
         }

         Iterator i$ = this.configurations.iterator();

         CombinedConfiguration.ConfigData cd;
         do {
            if (!i$.hasNext()) {
               return this;
            }

            cd = (CombinedConfiguration.ConfigData)i$.next();
         } while(root != cd.getRootNode());

         return cd.getConfiguration();
      }
   }

   class ConfigData {
      private AbstractConfiguration configuration;
      private String name;
      private Collection atPath;
      private String at;
      private ConfigurationNode rootNode;

      public ConfigData(AbstractConfiguration config, String n, String at) {
         this.configuration = config;
         this.name = n;
         this.atPath = this.parseAt(at);
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

      public ConfigurationNode getRootNode() {
         return this.rootNode;
      }

      public ConfigurationNode getTransformedRoot() {
         ViewNode result = new ViewNode();
         ViewNode atParent = result;
         ViewNode node;
         if (this.atPath != null) {
            for(Iterator i$ = this.atPath.iterator(); i$.hasNext(); atParent = node) {
               String p = (String)i$.next();
               node = new ViewNode();
               node.setName(p);
               atParent.addChild(node);
            }
         }

         ConfigurationNode root = ConfigurationUtils.convertToHierarchical(this.getConfiguration(), CombinedConfiguration.this.getConversionExpressionEngine()).getRootNode();
         atParent.appendChildren(root);
         atParent.appendAttributes(root);
         this.rootNode = root;
         return result;
      }

      private Collection parseAt(String at) {
         if (at == null) {
            return null;
         } else {
            Collection result = new ArrayList();
            DefaultConfigurationKey.KeyIterator it = (new DefaultConfigurationKey(CombinedConfiguration.AT_ENGINE, at)).iterator();

            while(it.hasNext()) {
               result.add(it.nextKey());
            }

            return result;
         }
      }
   }
}
