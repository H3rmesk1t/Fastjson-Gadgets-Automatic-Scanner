package org.apache.commons.configuration.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MergeCombiner extends NodeCombiner {
   public ConfigurationNode combine(ConfigurationNode node1, ConfigurationNode node2) {
      ViewNode result = this.createViewNode();
      result.setName(node1.getName());
      result.setValue(node1.getValue());
      this.addAttributes(result, node1, node2);
      List children2 = new LinkedList(node2.getChildren());
      Iterator i$ = node1.getChildren().iterator();

      ConfigurationNode c;
      while(i$.hasNext()) {
         c = (ConfigurationNode)i$.next();
         ConfigurationNode child2 = this.canCombine(node1, node2, c, children2);
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

   protected ConfigurationNode canCombine(ConfigurationNode node1, ConfigurationNode node2, ConfigurationNode child, List children2) {
      List attrs1 = child.getAttributes();
      List nodes = new ArrayList();
      List children = node2.getChildren(child.getName());
      Iterator it = children.iterator();

      while(it.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)it.next();
         Iterator iter = attrs1.iterator();

         while(iter.hasNext()) {
            ConfigurationNode attr1 = (ConfigurationNode)iter.next();
            List list2 = node.getAttributes(attr1.getName());
            if (list2.size() == 1 && !attr1.getValue().equals(((ConfigurationNode)list2.get(0)).getValue())) {
               node = null;
               break;
            }
         }

         if (node != null) {
            nodes.add(node);
         }
      }

      if (nodes.size() == 1) {
         return (ConfigurationNode)nodes.get(0);
      } else {
         if (nodes.size() > 1 && !this.isListNode(child)) {
            Iterator iter = nodes.iterator();

            while(iter.hasNext()) {
               children2.remove(iter.next());
            }
         }

         return null;
      }
   }
}
