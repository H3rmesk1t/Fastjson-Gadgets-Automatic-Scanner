package org.apache.commons.configuration.tree;

import java.util.List;

public interface ConfigurationNode {
   String getName();

   void setName(String var1);

   Object getValue();

   void setValue(Object var1);

   Object getReference();

   void setReference(Object var1);

   ConfigurationNode getParentNode();

   void setParentNode(ConfigurationNode var1);

   void addChild(ConfigurationNode var1);

   List getChildren();

   int getChildrenCount();

   List getChildren(String var1);

   int getChildrenCount(String var1);

   ConfigurationNode getChild(int var1);

   boolean removeChild(ConfigurationNode var1);

   boolean removeChild(String var1);

   void removeChildren();

   boolean isAttribute();

   void setAttribute(boolean var1);

   List getAttributes();

   int getAttributeCount();

   List getAttributes(String var1);

   int getAttributeCount(String var1);

   ConfigurationNode getAttribute(int var1);

   boolean removeAttribute(ConfigurationNode var1);

   boolean removeAttribute(String var1);

   void removeAttributes();

   void addAttribute(ConfigurationNode var1);

   boolean isDefined();

   void visit(ConfigurationNodeVisitor var1);

   Object clone();
}
