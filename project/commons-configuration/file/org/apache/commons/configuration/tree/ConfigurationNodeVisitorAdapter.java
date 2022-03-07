package org.apache.commons.configuration.tree;

public class ConfigurationNodeVisitorAdapter implements ConfigurationNodeVisitor {
   public void visitBeforeChildren(ConfigurationNode node) {
   }

   public void visitAfterChildren(ConfigurationNode node) {
   }

   public boolean terminate() {
      return false;
   }
}
