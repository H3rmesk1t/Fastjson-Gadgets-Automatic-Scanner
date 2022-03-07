package org.apache.commons.configuration.tree;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NodeAddData {
   private ConfigurationNode parent;
   private List pathNodes;
   private String newNodeName;
   private boolean attribute;

   public NodeAddData() {
      this((ConfigurationNode)null, (String)null);
   }

   public NodeAddData(ConfigurationNode parent, String nodeName) {
      this.setParent(parent);
      this.setNewNodeName(nodeName);
   }

   public boolean isAttribute() {
      return this.attribute;
   }

   public void setAttribute(boolean attribute) {
      this.attribute = attribute;
   }

   public String getNewNodeName() {
      return this.newNodeName;
   }

   public void setNewNodeName(String newNodeName) {
      this.newNodeName = newNodeName;
   }

   public ConfigurationNode getParent() {
      return this.parent;
   }

   public void setParent(ConfigurationNode parent) {
      this.parent = parent;
   }

   public List getPathNodes() {
      return this.pathNodes != null ? Collections.unmodifiableList(this.pathNodes) : Collections.emptyList();
   }

   public void addPathNode(String nodeName) {
      if (this.pathNodes == null) {
         this.pathNodes = new LinkedList();
      }

      this.pathNodes.add(nodeName);
   }
}
