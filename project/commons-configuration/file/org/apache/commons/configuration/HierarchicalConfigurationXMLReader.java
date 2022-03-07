package org.apache.commons.configuration;

import java.util.Iterator;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class HierarchicalConfigurationXMLReader extends ConfigurationXMLReader {
   private HierarchicalConfiguration configuration;

   public HierarchicalConfigurationXMLReader() {
   }

   public HierarchicalConfigurationXMLReader(HierarchicalConfiguration config) {
      this();
      this.setConfiguration(config);
   }

   public HierarchicalConfiguration getConfiguration() {
      return this.configuration;
   }

   public void setConfiguration(HierarchicalConfiguration config) {
      this.configuration = config;
   }

   public Configuration getParsedConfiguration() {
      return this.getConfiguration();
   }

   protected void processKeys() {
      this.getConfiguration().getRoot().visit(new HierarchicalConfigurationXMLReader.SAXVisitor(), (ConfigurationKey)null);
   }

   class SAXVisitor extends HierarchicalConfiguration.NodeVisitor {
      private static final String ATTR_TYPE = "CDATA";

      public void visitAfterChildren(HierarchicalConfiguration.Node node, ConfigurationKey key) {
         if (!this.isAttributeNode(node)) {
            HierarchicalConfigurationXMLReader.this.fireElementEnd(this.nodeName(node));
         }

      }

      public void visitBeforeChildren(HierarchicalConfiguration.Node node, ConfigurationKey key) {
         if (!this.isAttributeNode(node)) {
            HierarchicalConfigurationXMLReader.this.fireElementStart(this.nodeName(node), this.fetchAttributes(node));
            if (node.getValue() != null) {
               HierarchicalConfigurationXMLReader.this.fireCharacters(node.getValue().toString());
            }
         }

      }

      public boolean terminate() {
         return HierarchicalConfigurationXMLReader.this.getException() != null;
      }

      protected Attributes fetchAttributes(HierarchicalConfiguration.Node node) {
         AttributesImpl attrs = new AttributesImpl();
         Iterator i$ = node.getAttributes().iterator();

         while(i$.hasNext()) {
            ConfigurationNode child = (ConfigurationNode)i$.next();
            if (child.getValue() != null) {
               String attr = child.getName();
               attrs.addAttribute("", attr, attr, "CDATA", child.getValue().toString());
            }
         }

         return attrs;
      }

      private String nodeName(HierarchicalConfiguration.Node node) {
         return node.getName() == null ? HierarchicalConfigurationXMLReader.this.getRootName() : node.getName();
      }

      private boolean isAttributeNode(HierarchicalConfiguration.Node node) {
         return node.isAttribute();
      }
   }
}
