package org.apache.commons.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.NodeAddData;
import org.apache.commons.configuration.tree.ViewNode;
import org.apache.commons.lang.StringUtils;

public class HierarchicalConfiguration extends AbstractConfiguration implements Serializable, Cloneable {
   public static final int EVENT_CLEAR_TREE = 10;
   public static final int EVENT_ADD_NODES = 11;
   public static final int EVENT_SUBNODE_CHANGED = 12;
   private static final long serialVersionUID = 3373812230395363192L;
   private static ExpressionEngine defaultExpressionEngine;
   private HierarchicalConfiguration.Node root;
   private ConfigurationNode rootNode;
   private transient ExpressionEngine expressionEngine;

   public HierarchicalConfiguration() {
      this.setRootNode(new HierarchicalConfiguration.Node());
   }

   public HierarchicalConfiguration(HierarchicalConfiguration c) {
      this();
      if (c != null) {
         HierarchicalConfiguration.CloneVisitor visitor = new HierarchicalConfiguration.CloneVisitor();
         c.getRootNode().visit(visitor);
         this.setRootNode(visitor.getClone());
      }

   }

   public Object getReloadLock() {
      return this;
   }

   public HierarchicalConfiguration.Node getRoot() {
      return this.root == null && this.rootNode != null ? new HierarchicalConfiguration.Node(this.rootNode) : this.root;
   }

   public void setRoot(HierarchicalConfiguration.Node node) {
      if (node == null) {
         throw new IllegalArgumentException("Root node must not be null!");
      } else {
         this.root = node;
         this.rootNode = null;
      }
   }

   public ConfigurationNode getRootNode() {
      return (ConfigurationNode)(this.rootNode != null ? this.rootNode : this.root);
   }

   public void setRootNode(ConfigurationNode rootNode) {
      if (rootNode == null) {
         throw new IllegalArgumentException("Root node must not be null!");
      } else {
         this.rootNode = rootNode;
         this.root = rootNode instanceof HierarchicalConfiguration.Node ? (HierarchicalConfiguration.Node)rootNode : null;
      }
   }

   public static synchronized ExpressionEngine getDefaultExpressionEngine() {
      if (defaultExpressionEngine == null) {
         defaultExpressionEngine = new DefaultExpressionEngine();
      }

      return defaultExpressionEngine;
   }

   public static synchronized void setDefaultExpressionEngine(ExpressionEngine engine) {
      if (engine == null) {
         throw new IllegalArgumentException("Default expression engine must not be null!");
      } else {
         defaultExpressionEngine = engine;
      }
   }

   public ExpressionEngine getExpressionEngine() {
      return this.expressionEngine != null ? this.expressionEngine : getDefaultExpressionEngine();
   }

   public void setExpressionEngine(ExpressionEngine expressionEngine) {
      this.expressionEngine = expressionEngine;
   }

   public Object getProperty(String key) {
      List nodes = this.fetchNodeList(key);
      if (nodes.size() == 0) {
         return null;
      } else {
         List list = new ArrayList();
         Iterator i$ = nodes.iterator();

         while(i$.hasNext()) {
            ConfigurationNode node = (ConfigurationNode)i$.next();
            if (node.getValue() != null) {
               list.add(node.getValue());
            }
         }

         if (list.size() < 1) {
            return null;
         } else {
            return list.size() == 1 ? list.get(0) : list;
         }
      }
   }

   protected void addPropertyDirect(String key, Object obj) {
      NodeAddData data = this.getExpressionEngine().prepareAdd(this.getRootNode(), key);
      ConfigurationNode node = this.processNodeAddData(data);
      node.setValue(obj);
   }

   public void addNodes(String key, Collection nodes) {
      if (nodes != null && !nodes.isEmpty()) {
         this.fireEvent(11, key, nodes, true);
         List target = this.fetchNodeList(key);
         ConfigurationNode parent;
         if (target.size() == 1) {
            parent = (ConfigurationNode)target.get(0);
         } else {
            parent = this.processNodeAddData(this.getExpressionEngine().prepareAdd(this.getRootNode(), key));
         }

         if (parent.isAttribute()) {
            throw new IllegalArgumentException("Cannot add nodes to an attribute node!");
         } else {
            ConfigurationNode child;
            for(Iterator i$ = nodes.iterator(); i$.hasNext(); clearReferences(child)) {
               child = (ConfigurationNode)i$.next();
               if (child.isAttribute()) {
                  parent.addAttribute(child);
               } else {
                  parent.addChild(child);
               }
            }

            this.fireEvent(11, key, nodes, false);
         }
      }
   }

   public boolean isEmpty() {
      return !this.nodeDefined(this.getRootNode());
   }

   public Configuration subset(String prefix) {
      Collection nodes = this.fetchNodeList(prefix);
      if (nodes.isEmpty()) {
         return new HierarchicalConfiguration();
      } else {
         HierarchicalConfiguration result = new HierarchicalConfiguration() {
            protected Object interpolate(Object value) {
               return HierarchicalConfiguration.this.interpolate(value);
            }
         };
         HierarchicalConfiguration.CloneVisitor visitor = new HierarchicalConfiguration.CloneVisitor();
         Object value = null;
         int valueCount = 0;
         Iterator i$ = nodes.iterator();

         while(i$.hasNext()) {
            ConfigurationNode nd = (ConfigurationNode)i$.next();
            if (nd.getValue() != null) {
               value = nd.getValue();
               ++valueCount;
            }

            nd.visit(visitor);
            Iterator i$ = visitor.getClone().getChildren().iterator();

            ConfigurationNode attr;
            while(i$.hasNext()) {
               attr = (ConfigurationNode)i$.next();
               result.getRootNode().addChild(attr);
            }

            i$ = visitor.getClone().getAttributes().iterator();

            while(i$.hasNext()) {
               attr = (ConfigurationNode)i$.next();
               result.getRootNode().addAttribute(attr);
            }
         }

         if (valueCount == 1) {
            result.getRootNode().setValue(value);
         }

         return result.isEmpty() ? new HierarchicalConfiguration() : result;
      }
   }

   public SubnodeConfiguration configurationAt(String key, boolean supportUpdates) {
      List nodes = this.fetchNodeList(key);
      if (nodes.size() != 1) {
         throw new IllegalArgumentException("Passed in key must select exactly one node: " + key);
      } else {
         return supportUpdates ? this.createSubnodeConfiguration((ConfigurationNode)nodes.get(0), key) : this.createSubnodeConfiguration((ConfigurationNode)nodes.get(0));
      }
   }

   public SubnodeConfiguration configurationAt(String key) {
      return this.configurationAt(key, false);
   }

   public List configurationsAt(String key) {
      List nodes = this.fetchNodeList(key);
      List configs = new ArrayList(nodes.size());
      Iterator i$ = nodes.iterator();

      while(i$.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)i$.next();
         configs.add(this.createSubnodeConfiguration(node));
      }

      return configs;
   }

   protected SubnodeConfiguration createSubnodeConfiguration(ConfigurationNode node) {
      SubnodeConfiguration result = new SubnodeConfiguration(this, node);
      this.registerSubnodeConfiguration(result);
      return result;
   }

   protected SubnodeConfiguration createSubnodeConfiguration(ConfigurationNode node, String subnodeKey) {
      SubnodeConfiguration result = this.createSubnodeConfiguration(node);
      result.setSubnodeKey(subnodeKey);
      return result;
   }

   protected void subnodeConfigurationChanged(ConfigurationEvent event) {
      this.fireEvent(12, (String)null, event, event.isBeforeUpdate());
   }

   void registerSubnodeConfiguration(SubnodeConfiguration config) {
      config.addConfigurationListener(new ConfigurationListener() {
         public void configurationChanged(ConfigurationEvent event) {
            HierarchicalConfiguration.this.subnodeConfigurationChanged(event);
         }
      });
   }

   public boolean containsKey(String key) {
      return this.getProperty(key) != null;
   }

   public void setProperty(String key, Object value) {
      this.fireEvent(3, key, value, true);
      Iterator itNodes = this.fetchNodeList(key).iterator();
      Iterator itValues;
      if (this.isDelimiterParsingDisabled() && value instanceof String) {
         itValues = Collections.singleton(value).iterator();
      } else {
         itValues = PropertyConverter.toIterator(value, this.getListDelimiter());
      }

      while(itNodes.hasNext() && itValues.hasNext()) {
         ((ConfigurationNode)itNodes.next()).setValue(itValues.next());
      }

      while(itValues.hasNext()) {
         this.addPropertyDirect(key, itValues.next());
      }

      while(itNodes.hasNext()) {
         this.clearNode((ConfigurationNode)itNodes.next());
      }

      this.fireEvent(3, key, value, false);
   }

   public void clear() {
      this.fireEvent(4, (String)null, (Object)null, true);
      this.getRootNode().removeAttributes();
      this.getRootNode().removeChildren();
      this.getRootNode().setValue((Object)null);
      this.fireEvent(4, (String)null, (Object)null, false);
   }

   public void clearTree(String key) {
      this.fireEvent(10, key, (Object)null, true);
      List nodes = this.fetchNodeList(key);
      Iterator i$ = nodes.iterator();

      while(i$.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)i$.next();
         this.removeNode(node);
      }

      this.fireEvent(10, key, nodes, false);
   }

   public void clearProperty(String key) {
      this.fireEvent(2, key, (Object)null, true);
      List nodes = this.fetchNodeList(key);
      Iterator i$ = nodes.iterator();

      while(i$.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)i$.next();
         this.clearNode(node);
      }

      this.fireEvent(2, key, (Object)null, false);
   }

   public Iterator getKeys() {
      HierarchicalConfiguration.DefinedKeysVisitor visitor = new HierarchicalConfiguration.DefinedKeysVisitor();
      this.getRootNode().visit(visitor);
      return visitor.getKeyList().iterator();
   }

   public Iterator getKeys(String prefix) {
      HierarchicalConfiguration.DefinedKeysVisitor visitor = new HierarchicalConfiguration.DefinedKeysVisitor(prefix);
      if (this.containsKey(prefix)) {
         visitor.getKeyList().add(prefix);
      }

      List nodes = this.fetchNodeList(prefix);
      Iterator i$ = nodes.iterator();

      while(i$.hasNext()) {
         ConfigurationNode node = (ConfigurationNode)i$.next();
         Iterator i$ = node.getChildren().iterator();

         ConfigurationNode attr;
         while(i$.hasNext()) {
            attr = (ConfigurationNode)i$.next();
            attr.visit(visitor);
         }

         i$ = node.getAttributes().iterator();

         while(i$.hasNext()) {
            attr = (ConfigurationNode)i$.next();
            attr.visit(visitor);
         }
      }

      return visitor.getKeyList().iterator();
   }

   public int getMaxIndex(String key) {
      return this.fetchNodeList(key).size() - 1;
   }

   public Object clone() {
      try {
         HierarchicalConfiguration copy = (HierarchicalConfiguration)super.clone();
         HierarchicalConfiguration.CloneVisitor v = new HierarchicalConfiguration.CloneVisitor();
         this.getRootNode().visit(v);
         copy.setRootNode(v.getClone());
         return copy;
      } catch (CloneNotSupportedException var3) {
         throw new ConfigurationRuntimeException(var3);
      }
   }

   public Configuration interpolatedConfiguration() {
      HierarchicalConfiguration c = (HierarchicalConfiguration)this.clone();
      c.getRootNode().visit(new ConfigurationNodeVisitorAdapter() {
         public void visitAfterChildren(ConfigurationNode node) {
            node.setValue(HierarchicalConfiguration.this.interpolate(node.getValue()));
         }
      });
      return c;
   }

   protected List fetchNodeList(String key) {
      return this.getExpressionEngine().query(this.getRootNode(), key);
   }

   /** @deprecated */
   @Deprecated
   protected void findPropertyNodes(ConfigurationKey.KeyIterator keyPart, HierarchicalConfiguration.Node node, Collection nodes) {
   }

   /** @deprecated */
   @Deprecated
   protected boolean nodeDefined(HierarchicalConfiguration.Node node) {
      return this.nodeDefined((ConfigurationNode)node);
   }

   protected boolean nodeDefined(ConfigurationNode node) {
      HierarchicalConfiguration.DefinedVisitor visitor = new HierarchicalConfiguration.DefinedVisitor();
      node.visit(visitor);
      return visitor.isDefined();
   }

   /** @deprecated */
   @Deprecated
   protected void removeNode(HierarchicalConfiguration.Node node) {
      this.removeNode((ConfigurationNode)node);
   }

   protected void removeNode(ConfigurationNode node) {
      ConfigurationNode parent = node.getParentNode();
      if (parent != null) {
         parent.removeChild(node);
         if (!this.nodeDefined(parent)) {
            this.removeNode(parent);
         }
      }

   }

   /** @deprecated */
   @Deprecated
   protected void clearNode(HierarchicalConfiguration.Node node) {
      this.clearNode((ConfigurationNode)node);
   }

   protected void clearNode(ConfigurationNode node) {
      node.setValue((Object)null);
      if (!this.nodeDefined(node)) {
         this.removeNode(node);
      }

   }

   /** @deprecated */
   @Deprecated
   protected HierarchicalConfiguration.Node fetchAddNode(ConfigurationKey.KeyIterator keyIt, HierarchicalConfiguration.Node startNode) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   protected HierarchicalConfiguration.Node findLastPathNode(ConfigurationKey.KeyIterator keyIt, HierarchicalConfiguration.Node node) {
      return null;
   }

   /** @deprecated */
   @Deprecated
   protected HierarchicalConfiguration.Node createAddPath(ConfigurationKey.KeyIterator keyIt, HierarchicalConfiguration.Node root) {
      return null;
   }

   protected HierarchicalConfiguration.Node createNode(String name) {
      return new HierarchicalConfiguration.Node(name);
   }

   private ConfigurationNode processNodeAddData(NodeAddData data) {
      ConfigurationNode node = data.getParent();

      HierarchicalConfiguration.Node child;
      for(Iterator i$ = data.getPathNodes().iterator(); i$.hasNext(); node = child) {
         String name = (String)i$.next();
         child = this.createNode(name);
         ((ConfigurationNode)node).addChild(child);
      }

      ConfigurationNode child = this.createNode(data.getNewNodeName());
      if (data.isAttribute()) {
         ((ConfigurationNode)node).addAttribute(child);
      } else {
         ((ConfigurationNode)node).addChild(child);
      }

      return child;
   }

   protected static void clearReferences(ConfigurationNode node) {
      node.visit(new ConfigurationNodeVisitorAdapter() {
         public void visitBeforeChildren(ConfigurationNode node) {
            node.setReference((Object)null);
         }
      });
   }

   private static HierarchicalConfiguration.Node getNodeFor(Object obj) {
      HierarchicalConfiguration.Node nd;
      if (obj instanceof ViewNode) {
         final ViewNode viewNode = (ViewNode)obj;
         nd = new HierarchicalConfiguration.Node(viewNode) {
            public void setReference(Object reference) {
               super.setReference(reference);
               viewNode.setReference(reference);
            }
         };
      } else {
         nd = (HierarchicalConfiguration.Node)obj;
      }

      return nd;
   }

   protected abstract static class BuilderVisitor extends HierarchicalConfiguration.NodeVisitor {
      public void visitBeforeChildren(HierarchicalConfiguration.Node node, ConfigurationKey key) {
         Collection subNodes = new LinkedList(node.getChildren());
         subNodes.addAll(node.getAttributes());
         Iterator children = subNodes.iterator();
         HierarchicalConfiguration.Node sibling1 = null;
         HierarchicalConfiguration.Node nd = null;

         while(true) {
            do {
               if (!children.hasNext()) {
                  return;
               }

               do {
                  sibling1 = nd;
                  Object obj = children.next();
                  nd = HierarchicalConfiguration.getNodeFor(obj);
               } while(nd.getReference() != null && children.hasNext());
            } while(nd.getReference() != null);

            List newNodes = new LinkedList();
            newNodes.add(nd);

            while(children.hasNext()) {
               Object obj = children.next();
               nd = HierarchicalConfiguration.getNodeFor(obj);
               if (nd.getReference() != null) {
                  break;
               }

               newNodes.add(nd);
            }

            HierarchicalConfiguration.Node sibling2 = nd.getReference() == null ? null : nd;
            Iterator i$ = newNodes.iterator();

            while(i$.hasNext()) {
               HierarchicalConfiguration.Node insertNode = (HierarchicalConfiguration.Node)i$.next();
               if (insertNode.getReference() == null) {
                  Object ref = this.insert(insertNode, node, sibling1, sibling2);
                  if (ref != null) {
                     insertNode.setReference(ref);
                  }

                  sibling1 = insertNode;
               }
            }
         }
      }

      protected abstract Object insert(HierarchicalConfiguration.Node var1, HierarchicalConfiguration.Node var2, HierarchicalConfiguration.Node var3, HierarchicalConfiguration.Node var4);
   }

   static class CloneVisitor extends ConfigurationNodeVisitorAdapter {
      private Stack copyStack = new Stack();
      private ConfigurationNode result;

      public CloneVisitor() {
      }

      public void visitAfterChildren(ConfigurationNode node) {
         ConfigurationNode copy = (ConfigurationNode)this.copyStack.pop();
         if (this.copyStack.isEmpty()) {
            this.result = copy;
         }

      }

      public void visitBeforeChildren(ConfigurationNode node) {
         ConfigurationNode copy = (ConfigurationNode)node.clone();
         copy.setParentNode((ConfigurationNode)null);
         if (!this.copyStack.isEmpty()) {
            if (node.isAttribute()) {
               ((ConfigurationNode)this.copyStack.peek()).addAttribute(copy);
            } else {
               ((ConfigurationNode)this.copyStack.peek()).addChild(copy);
            }
         }

         this.copyStack.push(copy);
      }

      public ConfigurationNode getClone() {
         return this.result;
      }
   }

   class DefinedKeysVisitor extends ConfigurationNodeVisitorAdapter {
      private Set keyList;
      private Stack parentKeys;

      public DefinedKeysVisitor() {
         this.keyList = new LinkedHashSet();
         this.parentKeys = new Stack();
      }

      public DefinedKeysVisitor(String prefix) {
         this();
         this.parentKeys.push(prefix);
      }

      public Set getKeyList() {
         return this.keyList;
      }

      public void visitAfterChildren(ConfigurationNode node) {
         this.parentKeys.pop();
      }

      public void visitBeforeChildren(ConfigurationNode node) {
         String parentKey = this.parentKeys.isEmpty() ? null : (String)this.parentKeys.peek();
         String key = HierarchicalConfiguration.this.getExpressionEngine().nodeKey(node, parentKey);
         this.parentKeys.push(key);
         if (node.getValue() != null) {
            this.keyList.add(key);
         }

      }
   }

   static class DefinedVisitor extends ConfigurationNodeVisitorAdapter {
      private boolean defined;

      public boolean terminate() {
         return this.isDefined();
      }

      public void visitBeforeChildren(ConfigurationNode node) {
         this.defined = node.getValue() != null;
      }

      public boolean isDefined() {
         return this.defined;
      }
   }

   public static class NodeVisitor {
      public void visitBeforeChildren(HierarchicalConfiguration.Node node, ConfigurationKey key) {
      }

      public void visitAfterChildren(HierarchicalConfiguration.Node node, ConfigurationKey key) {
      }

      public boolean terminate() {
         return false;
      }
   }

   public static class Node extends DefaultConfigurationNode implements Serializable {
      private static final long serialVersionUID = -6357500633536941775L;

      public Node() {
      }

      public Node(String name) {
         super(name);
      }

      public Node(String name, Object value) {
         super(name, value);
      }

      public Node(ConfigurationNode src) {
         this(src.getName(), src.getValue());
         this.setReference(src.getReference());
         Iterator i$ = src.getChildren().iterator();

         ConfigurationNode nd;
         ConfigurationNode parent;
         while(i$.hasNext()) {
            nd = (ConfigurationNode)i$.next();
            parent = nd.getParentNode();
            this.addChild(nd);
            nd.setParentNode(parent);
         }

         i$ = src.getAttributes().iterator();

         while(i$.hasNext()) {
            nd = (ConfigurationNode)i$.next();
            parent = nd.getParentNode();
            this.addAttribute(nd);
            nd.setParentNode(parent);
         }

      }

      public HierarchicalConfiguration.Node getParent() {
         return (HierarchicalConfiguration.Node)this.getParentNode();
      }

      public void setParent(HierarchicalConfiguration.Node node) {
         this.setParentNode(node);
      }

      public void addChild(HierarchicalConfiguration.Node node) {
         this.addChild(node);
      }

      public boolean hasChildren() {
         return this.getChildrenCount() > 0 || this.getAttributeCount() > 0;
      }

      public boolean remove(HierarchicalConfiguration.Node child) {
         return child.isAttribute() ? this.removeAttribute(child) : this.removeChild(child);
      }

      public boolean remove(String name) {
         boolean childrenRemoved = this.removeChild(name);
         boolean attrsRemoved = this.removeAttribute(name);
         return childrenRemoved || attrsRemoved;
      }

      public void visit(HierarchicalConfiguration.NodeVisitor visitor, ConfigurationKey key) {
         int length = 0;
         if (key != null) {
            length = key.length();
            if (this.getName() != null) {
               key.append(StringUtils.replace(this.isAttribute() ? ConfigurationKey.constructAttributeKey(this.getName()) : this.getName(), String.valueOf('.'), ConfigurationKey.ESCAPED_DELIMITER));
            }
         }

         visitor.visitBeforeChildren(this, key);
         Iterator it = this.getChildren().iterator();

         Object obj;
         while(it.hasNext() && !visitor.terminate()) {
            obj = it.next();
            HierarchicalConfiguration.getNodeFor(obj).visit(visitor, key);
         }

         it = this.getAttributes().iterator();

         while(it.hasNext() && !visitor.terminate()) {
            obj = it.next();
            HierarchicalConfiguration.getNodeFor(obj).visit(visitor, key);
         }

         visitor.visitAfterChildren(this, key);
         if (key != null) {
            key.setLength(length);
         }

      }
   }
}
