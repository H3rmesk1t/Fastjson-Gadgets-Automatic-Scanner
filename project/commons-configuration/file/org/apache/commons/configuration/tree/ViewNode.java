package org.apache.commons.configuration.tree;

import java.util.Iterator;

public class ViewNode extends DefaultConfigurationNode {
   public void addAttribute(ConfigurationNode attr) {
      ConfigurationNode parent = null;
      if (attr != null) {
         parent = attr.getParentNode();
         super.addAttribute(attr);
         attr.setParentNode(parent);
      } else {
         throw new IllegalArgumentException("Attribute node must not be null!");
      }
   }

   public void addChild(ConfigurationNode child) {
      ConfigurationNode parent = null;
      if (child != null) {
         parent = child.getParentNode();
         super.addChild(child);
         child.setParentNode(parent);
      } else {
         throw new IllegalArgumentException("Child node must not be null!");
      }
   }

   public void appendAttributes(ConfigurationNode source) {
      if (source != null) {
         Iterator i$ = source.getAttributes().iterator();

         while(i$.hasNext()) {
            ConfigurationNode attr = (ConfigurationNode)i$.next();
            this.addAttribute(attr);
         }
      }

   }

   public void appendChildren(ConfigurationNode source) {
      if (source != null) {
         Iterator i$ = source.getChildren().iterator();

         while(i$.hasNext()) {
            ConfigurationNode child = (ConfigurationNode)i$.next();
            this.addChild(child);
         }
      }

   }
}
