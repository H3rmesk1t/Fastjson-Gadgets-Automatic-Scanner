package org.apache.commons.configuration.tree.xpath;

import java.util.Locale;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;

public class ConfigurationNodePointerFactory implements NodePointerFactory {
   public static final int CONFIGURATION_NODE_POINTER_FACTORY_ORDER = 200;

   public int getOrder() {
      return 200;
   }

   public NodePointer createNodePointer(QName name, Object bean, Locale locale) {
      return bean instanceof ConfigurationNode ? new ConfigurationNodePointer((ConfigurationNode)bean, locale) : null;
   }

   public NodePointer createNodePointer(NodePointer parent, QName name, Object bean) {
      return bean instanceof ConfigurationNode ? new ConfigurationNodePointer(parent, (ConfigurationNode)bean) : null;
   }
}
