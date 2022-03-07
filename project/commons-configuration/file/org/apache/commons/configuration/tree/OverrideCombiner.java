package org.apache.commons.configuration.tree;

import java.util.Iterator;

public class OverrideCombiner extends NodeCombiner {
   public ConfigurationNode combine(ConfigurationNode node1, ConfigurationNode node2) {
      ViewNode result = this.createViewNode();
      result.setName(node1.getName());
      Iterator i$ = node1.getChildren().iterator();

      ConfigurationNode child;
      while(i$.hasNext()) {
         child = (ConfigurationNode)i$.next();
         ConfigurationNode child2 = this.canCombine(node1, node2, child);
         if (child2 != null) {
            result.addChild(this.combine(child, child2));
         } else {
            result.addChild(child);
         }
      }

      i$ = node2.getChildren().iterator();

      while(i$.hasNext()) {
         child = (ConfigurationNode)i$.next();
         if (node1.getChildrenCount(child.getName()) < 1) {
            result.addChild(child);
         }
      }

      this.addAttributes(result, node1, node2);
      result.setValue(node1.getValue() != null ? node1.getValue() : node2.getValue());
      return result;
   }

   protected void addAttributes(ViewNode result, ConfigurationNode node1, ConfigurationNode node2) {
      result.appendAttributes(node1);
      Iterator i$ = node2.getAttributes().iterator();

      while(i$.hasNext()) {
         ConfigurationNode attr = (ConfigurationNode)i$.next();
         if (node1.getAttributeCount(attr.getName()) == 0) {
            result.addAttribute(attr);
         }
      }

   }

   protected ConfigurationNode canCombine(ConfigurationNode node1, ConfigurationNode node2, ConfigurationNode child) {
      return node2.getChildrenCount(child.getName()) == 1 && node1.getChildrenCount(child.getName()) == 1 && !this.isListNode(child) ? (ConfigurationNode)node2.getChildren(child.getName()).get(0) : null;
   }
}
