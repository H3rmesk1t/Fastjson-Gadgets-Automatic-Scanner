package org.apache.commons.configuration.beanutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.PropertyConverter;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.configuration.tree.ExpressionEngine;

public class XMLBeanDeclaration implements BeanDeclaration {
   public static final String RESERVED_PREFIX = "config-";
   public static final String ATTR_PREFIX = "[@config-";
   public static final String ATTR_BEAN_CLASS = "[@config-class]";
   public static final String ATTR_BEAN_FACTORY = "[@config-factory]";
   public static final String ATTR_FACTORY_PARAM = "[@config-factoryParam]";
   private final SubnodeConfiguration configuration;
   private final ConfigurationNode node;

   public XMLBeanDeclaration(HierarchicalConfiguration config, String key) {
      this(config, key, false);
   }

   public XMLBeanDeclaration(HierarchicalConfiguration config, String key, boolean optional) {
      if (config == null) {
         throw new IllegalArgumentException("Configuration must not be null!");
      } else {
         SubnodeConfiguration tmpconfiguration = null;
         Object tmpnode = null;

         try {
            tmpconfiguration = config.configurationAt(key);
            tmpnode = tmpconfiguration.getRootNode();
         } catch (IllegalArgumentException var7) {
            if (!optional || config.getMaxIndex(key) > 0) {
               throw var7;
            }

            tmpconfiguration = config.configurationAt((String)null);
            tmpnode = new DefaultConfigurationNode();
         }

         this.node = (ConfigurationNode)tmpnode;
         this.configuration = tmpconfiguration;
         this.initSubnodeConfiguration(this.getConfiguration());
      }
   }

   public XMLBeanDeclaration(HierarchicalConfiguration config) {
      this(config, (String)null);
   }

   public XMLBeanDeclaration(SubnodeConfiguration config, ConfigurationNode node) {
      if (config == null) {
         throw new IllegalArgumentException("Configuration must not be null!");
      } else if (node == null) {
         throw new IllegalArgumentException("Node must not be null!");
      } else {
         this.node = node;
         this.configuration = config;
         this.initSubnodeConfiguration(config);
      }
   }

   public SubnodeConfiguration getConfiguration() {
      return this.configuration;
   }

   public ConfigurationNode getNode() {
      return this.node;
   }

   public String getBeanFactoryName() {
      return this.getConfiguration().getString("[@config-factory]");
   }

   public Object getBeanFactoryParameter() {
      return this.getConfiguration().getProperty("[@config-factoryParam]");
   }

   public String getBeanClassName() {
      return this.getConfiguration().getString("[@config-class]");
   }

   public Map getBeanProperties() {
      Map props = new HashMap();
      Iterator i$ = this.getNode().getAttributes().iterator();

      while(i$.hasNext()) {
         ConfigurationNode attr = (ConfigurationNode)i$.next();
         if (!this.isReservedNode(attr)) {
            props.put(attr.getName(), this.interpolate(attr.getValue()));
         }
      }

      return props;
   }

   public Map getNestedBeanDeclarations() {
      Map nested = new HashMap();
      Iterator i$ = this.getNode().getChildren().iterator();

      while(i$.hasNext()) {
         ConfigurationNode child = (ConfigurationNode)i$.next();
         if (!this.isReservedNode(child)) {
            if (nested.containsKey(child.getName())) {
               Object obj = nested.get(child.getName());
               Object list;
               if (obj instanceof List) {
                  List tmpList = (List)obj;
                  list = tmpList;
               } else {
                  list = new ArrayList();
                  ((List)list).add((BeanDeclaration)obj);
                  nested.put(child.getName(), list);
               }

               ((List)list).add(this.createBeanDeclaration(child));
            } else {
               nested.put(child.getName(), this.createBeanDeclaration(child));
            }
         }
      }

      return nested;
   }

   protected Object interpolate(Object value) {
      return PropertyConverter.interpolate(value, this.getConfiguration().getParent());
   }

   protected boolean isReservedNode(ConfigurationNode nd) {
      return nd.isAttribute() && (nd.getName() == null || nd.getName().startsWith("config-"));
   }

   protected BeanDeclaration createBeanDeclaration(ConfigurationNode node) {
      List list = this.getConfiguration().configurationsAt(node.getName());
      if (list.size() == 1) {
         return new XMLBeanDeclaration((SubnodeConfiguration)list.get(0), node);
      } else {
         Iterator iter = list.iterator();

         SubnodeConfiguration config;
         do {
            if (!iter.hasNext()) {
               throw new ConfigurationRuntimeException("Unable to match node for " + node.getName());
            }

            config = (SubnodeConfiguration)iter.next();
         } while(!config.getRootNode().equals(node));

         return new XMLBeanDeclaration(config, node);
      }
   }

   private void initSubnodeConfiguration(SubnodeConfiguration conf) {
      conf.setThrowExceptionOnMissing(false);
      conf.setExpressionEngine((ExpressionEngine)null);
   }
}
