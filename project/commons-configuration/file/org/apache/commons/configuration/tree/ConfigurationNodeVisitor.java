package org.apache.commons.configuration.tree;

public interface ConfigurationNodeVisitor {
   void visitBeforeChildren(ConfigurationNode var1);

   void visitAfterChildren(ConfigurationNode var1);

   boolean terminate();
}
