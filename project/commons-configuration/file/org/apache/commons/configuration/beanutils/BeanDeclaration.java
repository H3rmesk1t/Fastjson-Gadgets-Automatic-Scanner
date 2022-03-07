package org.apache.commons.configuration.beanutils;

import java.util.Map;

public interface BeanDeclaration {
   String getBeanFactoryName();

   Object getBeanFactoryParameter();

   String getBeanClassName();

   Map getBeanProperties();

   Map getNestedBeanDeclarations();
}
