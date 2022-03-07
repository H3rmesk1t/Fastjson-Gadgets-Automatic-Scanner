package org.apache.commons.configuration.tree.xpath;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

class ConfigurationNodePointer extends NodePointer {
   private static final long serialVersionUID = -1087475639680007713L;
   private ConfigurationNode node;

   public ConfigurationNodePointer(ConfigurationNode node, Locale locale) {
      super((NodePointer)null, locale);
      this.node = node;
   }

   public ConfigurationNodePointer(NodePointer parent, ConfigurationNode node) {
      super(parent);
      this.node = node;
   }

   public boolean isLeaf() {
      return this.node.getChildrenCount() < 1;
   }

   public boolean isCollection() {
      return false;
   }

   public int getLength() {
      return 1;
   }

   public boolean isAttribute() {
      return this.node.isAttribute();
   }

   public QName getName() {
      return new QName((String)null, this.node.getName());
   }

   public Object getBaseValue() {
      return this.node;
   }

   public Object getImmediateNode() {
      return this.node;
   }

   public Object getValue() {
      return this.node.getValue();
   }

   public void setValue(Object value) {
      this.node.setValue(value);
   }

   public int compareChildNodePointers(NodePointer pointer1, NodePointer pointer2) {
      ConfigurationNode node1 = (ConfigurationNode)pointer1.getBaseValue();
      ConfigurationNode node2 = (ConfigurationNode)pointer2.getBaseValue();
      if (node1.isAttribute() && !node2.isAttribute()) {
         return -1;
      } else if (node2.isAttribute() && !node1.isAttribute()) {
         return 1;
      } else {
         List subNodes = node1.isAttribute() ? this.node.getAttributes() : this.node.getChildren();
         Iterator i$ = subNodes.iterator();

         ConfigurationNode child;
         do {
            if (!i$.hasNext()) {
               return 0;
            }

            child = (ConfigurationNode)i$.next();
            if (child == node1) {
               return -1;
            }
         } while(child != node2);

         return 1;
      }
   }

   public NodeIterator attributeIterator(QName name) {
      return new ConfigurationNodeIteratorAttribute(this, name);
   }

   public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith) {
      return new ConfigurationNodeIteratorChildren(this, test, reverse, startWith);
   }

   public boolean testNode(NodeTest test) {
      return test instanceof NodeTypeTest && ((NodeTypeTest)test).getNodeType() == 2 ? true : super.testNode(test);
   }
}
