package org.apache.commons.configuration.tree.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.lang.StringUtils;

class ConfigurationNodeIteratorChildren extends ConfigurationNodeIteratorBase {
   public ConfigurationNodeIteratorChildren(NodePointer parent, NodeTest nodeTest, boolean reverse, NodePointer startsWith) {
      super(parent, reverse);
      ConfigurationNode root = (ConfigurationNode)parent.getNode();
      List childNodes = this.createSubNodeList(root, nodeTest);
      this.initSubNodeList(childNodes);
      if (startsWith != null) {
         this.setStartOffset(this.findStartIndex(root, (ConfigurationNode)startsWith.getNode()));
      }

   }

   protected List createSubNodeList(ConfigurationNode node, NodeTest test) {
      List children = node.getChildren();
      if (test == null) {
         return children;
      } else {
         if (test instanceof NodeNameTest) {
            NodeNameTest nameTest = (NodeNameTest)test;
            QName name = nameTest.getNodeName();
            if (name.getPrefix() == null) {
               if (nameTest.isWildcard()) {
                  return children;
               }

               List result = new ArrayList();
               Iterator i$ = children.iterator();

               while(i$.hasNext()) {
                  ConfigurationNode child = (ConfigurationNode)i$.next();
                  if (StringUtils.equals(name.getName(), child.getName())) {
                     result.add(child);
                  }
               }

               return result;
            }
         } else if (test instanceof NodeTypeTest) {
            NodeTypeTest typeTest = (NodeTypeTest)test;
            if (typeTest.getNodeType() == 1 || typeTest.getNodeType() == 2) {
               return children;
            }
         }

         return Collections.emptyList();
      }
   }

   protected int findStartIndex(ConfigurationNode node, ConfigurationNode startNode) {
      for(int index = 0; index < node.getChildrenCount(); ++index) {
         if (node.getChild(index) == startNode) {
            return index;
         }
      }

      return -1;
   }
}
