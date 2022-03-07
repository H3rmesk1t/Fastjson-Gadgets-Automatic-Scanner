package org.apache.commons.configuration.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationRuntimeException;

public class DefaultConfigurationNode implements ConfigurationNode, Cloneable {
   private DefaultConfigurationNode.SubNodes children;
   private DefaultConfigurationNode.SubNodes attributes;
   private ConfigurationNode parent;
   private Object value;
   private Object reference;
   private String name;
   private boolean attribute;

   public DefaultConfigurationNode() {
      this((String)null);
   }

   public DefaultConfigurationNode(String name) {
      this(name, (Object)null);
   }

   public DefaultConfigurationNode(String name, Object value) {
      this.setName(name);
      this.setValue(value);
      this.initSubNodes();
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.checkState();
      this.name = name;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object val) {
      this.value = val;
   }

   public Object getReference() {
      return this.reference;
   }

   public void setReference(Object reference) {
      this.reference = reference;
   }

   public ConfigurationNode getParentNode() {
      return this.parent;
   }

   public void setParentNode(ConfigurationNode parent) {
      this.parent = parent;
   }

   public void addChild(ConfigurationNode child) {
      this.children.addNode(child);
      child.setAttribute(false);
      child.setParentNode(this);
   }

   public List getChildren() {
      return this.children.getSubNodes();
   }

   public int getChildrenCount() {
      return this.children.getSubNodes().size();
   }

   public List getChildren(String name) {
      return this.children.getSubNodes(name);
   }

   public int getChildrenCount(String name) {
      return this.children.getSubNodes(name).size();
   }

   public ConfigurationNode getChild(int index) {
      return this.children.getNode(index);
   }

   public boolean removeChild(ConfigurationNode child) {
      return this.children.removeNode(child);
   }

   public boolean removeChild(String childName) {
      return this.children.removeNodes(childName);
   }

   public void removeChildren() {
      this.children.clear();
   }

   public boolean isAttribute() {
      return this.attribute;
   }

   public void setAttribute(boolean f) {
      this.checkState();
      this.attribute = f;
   }

   public void addAttribute(ConfigurationNode attr) {
      this.attributes.addNode(attr);
      attr.setAttribute(true);
      attr.setParentNode(this);
   }

   public List getAttributes() {
      return this.attributes.getSubNodes();
   }

   public int getAttributeCount() {
      return this.attributes.getSubNodes().size();
   }

   public List getAttributes(String name) {
      return this.attributes.getSubNodes(name);
   }

   public int getAttributeCount(String name) {
      return this.getAttributes(name).size();
   }

   public boolean removeAttribute(ConfigurationNode node) {
      return this.attributes.removeNode(node);
   }

   public boolean removeAttribute(String name) {
      return this.attributes.removeNodes(name);
   }

   public ConfigurationNode getAttribute(int index) {
      return this.attributes.getNode(index);
   }

   public void removeAttributes() {
      this.attributes.clear();
   }

   public boolean isDefined() {
      return this.getValue() != null || this.getChildrenCount() > 0 || this.getAttributeCount() > 0;
   }

   public void visit(ConfigurationNodeVisitor visitor) {
      if (visitor == null) {
         throw new IllegalArgumentException("Visitor must not be null!");
      } else {
         if (!visitor.terminate()) {
            visitor.visitBeforeChildren(this);
            this.children.visit(visitor);
            this.attributes.visit(visitor);
            visitor.visitAfterChildren(this);
         }

      }
   }

   public Object clone() {
      try {
         DefaultConfigurationNode copy = (DefaultConfigurationNode)super.clone();
         copy.initSubNodes();
         return copy;
      } catch (CloneNotSupportedException var2) {
         throw new ConfigurationRuntimeException("Cannot clone " + this.getClass());
      }
   }

   protected void checkState() {
      if (this.getParentNode() != null) {
         throw new IllegalStateException("Node cannot be modified when added to a parent!");
      }
   }

   protected DefaultConfigurationNode.SubNodes createSubNodes(boolean attributes) {
      return new DefaultConfigurationNode.SubNodes();
   }

   protected void removeReference() {
   }

   private void initSubNodes() {
      this.children = this.createSubNodes(false);
      this.attributes = this.createSubNodes(true);
   }

   protected static class SubNodes {
      private List nodes;
      private Map namedNodes;

      public void addNode(ConfigurationNode node) {
         if (node != null && node.getName() != null) {
            node.setParentNode((ConfigurationNode)null);
            if (this.nodes == null) {
               this.nodes = new ArrayList();
               this.namedNodes = new HashMap();
            }

            this.nodes.add(node);
            List lst = (List)this.namedNodes.get(node.getName());
            if (lst == null) {
               lst = new LinkedList();
               this.namedNodes.put(node.getName(), lst);
            }

            ((List)lst).add(node);
         } else {
            throw new IllegalArgumentException("Node to add must have a defined name!");
         }
      }

      public boolean removeNode(ConfigurationNode node) {
         if (this.nodes != null && node != null && this.nodes.contains(node)) {
            this.detachNode(node);
            this.nodes.remove(node);
            List lst = (List)this.namedNodes.get(node.getName());
            if (lst != null) {
               lst.remove(node);
               if (lst.isEmpty()) {
                  this.namedNodes.remove(node.getName());
               }
            }

            return true;
         } else {
            return false;
         }
      }

      public boolean removeNodes(String name) {
         if (this.nodes != null && name != null) {
            List lst = (List)this.namedNodes.remove(name);
            if (lst != null) {
               this.detachNodes(lst);
               this.nodes.removeAll(lst);
               return true;
            }
         }

         return false;
      }

      public void clear() {
         if (this.nodes != null) {
            this.detachNodes(this.nodes);
            this.nodes = null;
            this.namedNodes = null;
         }

      }

      public ConfigurationNode getNode(int index) {
         if (this.nodes == null) {
            throw new IndexOutOfBoundsException("No sub nodes available!");
         } else {
            return (ConfigurationNode)this.nodes.get(index);
         }
      }

      public List getSubNodes() {
         return this.nodes == null ? Collections.emptyList() : Collections.unmodifiableList(this.nodes);
      }

      public List getSubNodes(String name) {
         if (name == null) {
            return this.getSubNodes();
         } else {
            List result;
            if (this.nodes == null) {
               result = null;
            } else {
               result = (List)this.namedNodes.get(name);
            }

            return result == null ? Collections.emptyList() : Collections.unmodifiableList(result);
         }
      }

      public void visit(ConfigurationNodeVisitor visitor) {
         if (this.nodes != null) {
            Iterator it = this.nodes.iterator();

            while(it.hasNext() && !visitor.terminate()) {
               ((ConfigurationNode)it.next()).visit(visitor);
            }
         }

      }

      protected void detachNode(ConfigurationNode subNode) {
         subNode.setParentNode((ConfigurationNode)null);
         if (subNode instanceof DefaultConfigurationNode) {
            ((DefaultConfigurationNode)subNode).removeReference();
         }

      }

      protected void detachNodes(Collection subNodes) {
         Iterator i$ = subNodes.iterator();

         while(i$.hasNext()) {
            ConfigurationNode nd = (ConfigurationNode)i$.next();
            this.detachNode(nd);
         }

      }
   }
}
