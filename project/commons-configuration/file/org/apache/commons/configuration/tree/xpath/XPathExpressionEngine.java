package org.apache.commons.configuration.tree.xpath;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.NodeAddData;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.lang.StringUtils;

public class XPathExpressionEngine implements ExpressionEngine {
   static final String PATH_DELIMITER = "/";
   static final String ATTR_DELIMITER = "@";
   private static final String NODE_PATH_DELIMITERS = "/@";
   private static final String SPACE = " ";

   public List query(ConfigurationNode root, String key) {
      if (StringUtils.isEmpty(key)) {
         return Collections.singletonList(root);
      } else {
         JXPathContext context = this.createContext(root, key);
         List result = context.selectNodes(key);
         if (result == null) {
            result = Collections.emptyList();
         }

         return result;
      }
   }

   public String nodeKey(ConfigurationNode node, String parentKey) {
      if (parentKey == null) {
         return "";
      } else if (node.getName() == null) {
         return parentKey;
      } else {
         StringBuilder buf = new StringBuilder(parentKey.length() + node.getName().length() + "/".length());
         if (parentKey.length() > 0) {
            buf.append(parentKey);
            buf.append("/");
         }

         if (node.isAttribute()) {
            buf.append("@");
         }

         buf.append(node.getName());
         return buf.toString();
      }
   }

   public NodeAddData prepareAdd(ConfigurationNode root, String key) {
      if (key == null) {
         throw new IllegalArgumentException("prepareAdd: key must not be null!");
      } else {
         String addKey = key;
         int index = findKeySeparator(key);
         if (index < 0) {
            addKey = this.generateKeyForAdd(root, key);
            index = findKeySeparator(addKey);
         }

         List nodes = this.query(root, addKey.substring(0, index).trim());
         if (nodes.size() != 1) {
            throw new IllegalArgumentException("prepareAdd: key must select exactly one target node!");
         } else {
            NodeAddData data = new NodeAddData();
            data.setParent((ConfigurationNode)nodes.get(0));
            this.initNodeAddData(data, addKey.substring(index).trim());
            return data;
         }
      }
   }

   protected JXPathContext createContext(ConfigurationNode root, String key) {
      JXPathContext context = JXPathContext.newContext(root);
      context.setLenient(true);
      return context;
   }

   protected void initNodeAddData(NodeAddData data, String path) {
      String lastComponent = null;
      boolean attr = false;
      boolean first = true;

      for(StringTokenizer tok = new StringTokenizer(path, "/@", true); tok.hasMoreTokens(); first = false) {
         String token = tok.nextToken();
         if ("/".equals(token)) {
            if (attr) {
               this.invalidPath(path, " contains an attribute delimiter at an unallowed position.");
            }

            if (lastComponent == null) {
               this.invalidPath(path, " contains a '/' at an unallowed position.");
            }

            data.addPathNode(lastComponent);
            lastComponent = null;
         } else if ("@".equals(token)) {
            if (attr) {
               this.invalidPath(path, " contains multiple attribute delimiters.");
            }

            if (lastComponent == null && !first) {
               this.invalidPath(path, " contains an attribute delimiter at an unallowed position.");
            }

            if (lastComponent != null) {
               data.addPathNode(lastComponent);
            }

            attr = true;
            lastComponent = null;
         } else {
            lastComponent = token;
         }
      }

      if (lastComponent == null) {
         this.invalidPath(path, "contains no components.");
      }

      data.setNewNodeName(lastComponent);
      data.setAttribute(attr);
   }

   private String generateKeyForAdd(ConfigurationNode root, String key) {
      for(int pos = key.lastIndexOf("/", key.length()); pos >= 0; pos = key.lastIndexOf("/", pos - 1)) {
         String keyExisting = key.substring(0, pos);
         if (!this.query(root, keyExisting).isEmpty()) {
            StringBuilder buf = new StringBuilder(key.length() + 1);
            buf.append(keyExisting).append(" ");
            buf.append(key.substring(pos + 1));
            return buf.toString();
         }
      }

      return " " + key;
   }

   private void invalidPath(String path, String msg) {
      throw new IllegalArgumentException("Invalid node path: \"" + path + "\" " + msg);
   }

   private static int findKeySeparator(String key) {
      int index;
      for(index = key.length() - 1; index >= 0 && !Character.isWhitespace(key.charAt(index)); --index) {
      }

      return index;
   }

   static {
      JXPathContextReferenceImpl.addNodePointerFactory(new ConfigurationNodePointerFactory());
   }
}
