package org.apache.commons.configuration.tree.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

class ConfigurationNodeIteratorAttribute extends ConfigurationNodeIteratorBase {
   private static final String WILDCARD = "*";

   public ConfigurationNodeIteratorAttribute(NodePointer parent, QName name) {
      super(parent, false);
      this.initSubNodeList(this.createSubNodeList((ConfigurationNode)parent.getNode(), name));
   }

   protected List createSubNodeList(ConfigurationNode node, QName name) {
      if (name.getPrefix() != null) {
         return Collections.emptyList();
      } else {
         List result = new ArrayList();
         if (!"*".equals(name.getName())) {
            result.addAll(node.getAttributes(name.getName()));
         } else {
            result.addAll(node.getAttributes());
         }

         return result;
      }
   }
}
