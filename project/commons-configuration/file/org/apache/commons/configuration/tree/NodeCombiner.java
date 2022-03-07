package org.apache.commons.configuration.tree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class NodeCombiner {
   protected Set listNodes = new HashSet();

   public void addListNode(String nodeName) {
      this.listNodes.add(nodeName);
   }

   public Set getListNodes() {
      return Collections.unmodifiableSet(this.listNodes);
   }

   public boolean isListNode(ConfigurationNode node) {
      return this.listNodes.contains(node.getName());
   }

   public abstract ConfigurationNode combine(ConfigurationNode var1, ConfigurationNode var2);

   protected ViewNode createViewNode() {
      return new ViewNode();
   }
}
