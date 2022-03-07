package org.apache.commons.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration.reloading.Reloadable;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class SubnodeConfiguration extends HierarchicalReloadableConfiguration {
   private static final long serialVersionUID = 3105734147019386480L;
   private HierarchicalConfiguration parent;
   private String subnodeKey;

   public SubnodeConfiguration(HierarchicalConfiguration parent, ConfigurationNode root) {
      super(parent instanceof Reloadable ? ((Reloadable)parent).getReloadLock() : null);
      if (parent == null) {
         throw new IllegalArgumentException("Parent configuration must not be null!");
      } else if (root == null) {
         throw new IllegalArgumentException("Root node must not be null!");
      } else {
         this.setRootNode(root);
         this.parent = parent;
         this.initFromParent(parent);
      }
   }

   public HierarchicalConfiguration getParent() {
      return this.parent;
   }

   public String getSubnodeKey() {
      return this.subnodeKey;
   }

   public void setSubnodeKey(String subnodeKey) {
      this.subnodeKey = subnodeKey;
   }

   public ConfigurationNode getRootNode() {
      if (this.getSubnodeKey() != null) {
         try {
            List nodes = this.getParent().fetchNodeList(this.getSubnodeKey());
            if (nodes.size() == 1) {
               ConfigurationNode currentRoot = (ConfigurationNode)nodes.get(0);
               if (currentRoot != super.getRootNode()) {
                  this.fireEvent(12, (String)null, (Object)null, true);
                  this.setRootNode(currentRoot);
                  this.fireEvent(12, (String)null, (Object)null, false);
               }

               return currentRoot;
            }

            this.setSubnodeKey((String)null);
         } catch (Exception var3) {
            this.setSubnodeKey((String)null);
         }
      }

      return super.getRootNode();
   }

   protected SubnodeConfiguration createSubnodeConfiguration(ConfigurationNode node) {
      SubnodeConfiguration result = new SubnodeConfiguration(this.getParent(), node);
      this.getParent().registerSubnodeConfiguration(result);
      return result;
   }

   protected SubnodeConfiguration createSubnodeConfiguration(ConfigurationNode node, String subnodeKey) {
      SubnodeConfiguration result = this.createSubnodeConfiguration(node);
      if (this.getSubnodeKey() != null) {
         List lstPathToRoot = new ArrayList();
         ConfigurationNode top = super.getRootNode();

         for(ConfigurationNode nd = node; nd != top; nd = nd.getParentNode()) {
            lstPathToRoot.add(nd);
         }

         Collections.reverse(lstPathToRoot);
         String key = this.getSubnodeKey();

         ConfigurationNode pathNode;
         for(Iterator i$ = lstPathToRoot.iterator(); i$.hasNext(); key = this.getParent().getExpressionEngine().nodeKey(pathNode, key)) {
            pathNode = (ConfigurationNode)i$.next();
         }

         result.setSubnodeKey(key);
      }

      return result;
   }

   protected HierarchicalConfiguration.Node createNode(String name) {
      return this.getParent().createNode(name);
   }

   protected void initFromParent(HierarchicalConfiguration parentConfig) {
      this.setExpressionEngine(parentConfig.getExpressionEngine());
      this.setListDelimiter(parentConfig.getListDelimiter());
      this.setDelimiterParsingDisabled(parentConfig.isDelimiterParsingDisabled());
      this.setThrowExceptionOnMissing(parentConfig.isThrowExceptionOnMissing());
   }

   protected ConfigurationInterpolator createInterpolator() {
      ConfigurationInterpolator interpolator = super.createInterpolator();
      interpolator.setParentInterpolator(this.getParent().getInterpolator());
      return interpolator;
   }
}
