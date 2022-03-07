package org.apache.commons.configuration.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UnionCombiner extends NodeCombiner {
   public ConfigurationNode combine(ConfigurationNode node1, ConfigurationNode node2) {
      ViewNode result = this.createViewNode();
      result.setName(node1.getName());
      result.appendAttributes(node1);
      result.appendAttributes(node2);
      List children2 = new LinkedList(node2.getChildren());
      Iterator i$ = node1.getChildren().iterator();

      ConfigurationNode c;
      while(i$.hasNext()) {
         c = (ConfigurationNode)i$.next();
         ConfigurationNode child2 = this.findCombineNode(node1, node2, c, children2);
         if (child2 != null) {
            result.addChild(this.combine(c, child2));
            children2.remove(child2);
         } else {
            result.addChild(c);
         }
      }

      i$ = children2.iterator();

      while(i$.hasNext()) {
         c = (ConfigurationNode)i$.next();
         result.addChild(c);
      }

      return result;
   }

   protected ConfigurationNode findCombineNode(ConfigurationNode node1, ConfigurationNode node2, ConfigurationNode child, List children) {
      if (child.getValue() == null && !this.isListNode(child) && node1.getChildrenCount(child.getName()) == 1 && node2.getChildrenCount(child.getName()) == 1) {
         ConfigurationNode child2 = (ConfigurationNode)node2.getChildren(child.getName()).iterator().next();
         if (child2.getValue() == null) {
            return child2;
         }
      }

      return null;
   }
}
