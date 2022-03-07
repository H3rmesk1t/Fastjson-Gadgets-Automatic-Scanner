package org.apache.commons.configuration.tree;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DefaultExpressionEngine implements ExpressionEngine {
   public static final String DEFAULT_PROPERTY_DELIMITER = ".";
   public static final String DEFAULT_ESCAPED_DELIMITER = "..";
   public static final String DEFAULT_ATTRIBUTE_START = "[@";
   public static final String DEFAULT_ATTRIBUTE_END = "]";
   public static final String DEFAULT_INDEX_START = "(";
   public static final String DEFAULT_INDEX_END = ")";
   private String propertyDelimiter = ".";
   private String escapedDelimiter = "..";
   private String attributeStart = "[@";
   private String attributeEnd = "]";
   private String indexStart = "(";
   private String indexEnd = ")";

   public String getAttributeEnd() {
      return this.attributeEnd;
   }

   public void setAttributeEnd(String attributeEnd) {
      this.attributeEnd = attributeEnd;
   }

   public String getAttributeStart() {
      return this.attributeStart;
   }

   public void setAttributeStart(String attributeStart) {
      this.attributeStart = attributeStart;
   }

   public String getEscapedDelimiter() {
      return this.escapedDelimiter;
   }

   public void setEscapedDelimiter(String escapedDelimiter) {
      this.escapedDelimiter = escapedDelimiter;
   }

   public String getIndexEnd() {
      return this.indexEnd;
   }

   public void setIndexEnd(String indexEnd) {
      this.indexEnd = indexEnd;
   }

   public String getIndexStart() {
      return this.indexStart;
   }

   public void setIndexStart(String indexStart) {
      this.indexStart = indexStart;
   }

   public String getPropertyDelimiter() {
      return this.propertyDelimiter;
   }

   public void setPropertyDelimiter(String propertyDelimiter) {
      this.propertyDelimiter = propertyDelimiter;
   }

   public List query(ConfigurationNode root, String key) {
      List nodes = new LinkedList();
      this.findNodesForKey((new DefaultConfigurationKey(this, key)).iterator(), root, nodes);
      return nodes;
   }

   public String nodeKey(ConfigurationNode node, String parentKey) {
      if (parentKey == null) {
         return "";
      } else {
         DefaultConfigurationKey key = new DefaultConfigurationKey(this, parentKey);
         if (node.isAttribute()) {
            key.appendAttribute(node.getName());
         } else {
            key.append(node.getName(), true);
         }

         return key.toString();
      }
   }

   public NodeAddData prepareAdd(ConfigurationNode root, String key) {
      DefaultConfigurationKey.KeyIterator it = (new DefaultConfigurationKey(this, key)).iterator();
      if (!it.hasNext()) {
         throw new IllegalArgumentException("Key for add operation must be defined!");
      } else {
         NodeAddData result = new NodeAddData();
         result.setParent(this.findLastPathNode(it, root));

         while(it.hasNext()) {
            if (!it.isPropertyKey()) {
               throw new IllegalArgumentException("Invalid key for add operation: " + key + " (Attribute key in the middle.)");
            }

            result.addPathNode(it.currentKey());
            it.next();
         }

         result.setNewNodeName(it.currentKey());
         result.setAttribute(!it.isPropertyKey());
         return result;
      }
   }

   protected void findNodesForKey(DefaultConfigurationKey.KeyIterator keyPart, ConfigurationNode node, Collection nodes) {
      if (!keyPart.hasNext()) {
         nodes.add(node);
      } else {
         String key = keyPart.nextKey(false);
         if (keyPart.isPropertyKey()) {
            this.processSubNodes(keyPart, node.getChildren(key), nodes);
         }

         if (keyPart.isAttribute()) {
            this.processSubNodes(keyPart, node.getAttributes(key), nodes);
         }
      }

   }

   protected ConfigurationNode findLastPathNode(DefaultConfigurationKey.KeyIterator keyIt, ConfigurationNode node) {
      String keyPart = keyIt.nextKey(false);
      if (keyIt.hasNext()) {
         if (!keyIt.isPropertyKey()) {
            throw new IllegalArgumentException("Invalid path for add operation: Attribute key in the middle!");
         } else {
            int idx = keyIt.hasIndex() ? keyIt.getIndex() : node.getChildrenCount(keyPart) - 1;
            return idx >= 0 && idx < node.getChildrenCount(keyPart) ? this.findLastPathNode(keyIt, (ConfigurationNode)node.getChildren(keyPart).get(idx)) : node;
         }
      } else {
         return node;
      }
   }

   private void processSubNodes(DefaultConfigurationKey.KeyIterator keyPart, List subNodes, Collection nodes) {
      if (keyPart.hasIndex()) {
         if (keyPart.getIndex() >= 0 && keyPart.getIndex() < subNodes.size()) {
            this.findNodesForKey((DefaultConfigurationKey.KeyIterator)keyPart.clone(), (ConfigurationNode)subNodes.get(keyPart.getIndex()), nodes);
         }
      } else {
         Iterator i$ = subNodes.iterator();

         while(i$.hasNext()) {
            ConfigurationNode node = (ConfigurationNode)i$.next();
            this.findNodesForKey((DefaultConfigurationKey.KeyIterator)keyPart.clone(), node, nodes);
         }
      }

   }
}
