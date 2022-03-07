package org.apache.commons.configuration.beanutils;

public interface BeanFactory {
   Object createBean(Class var1, BeanDeclaration var2, Object var3) throws Exception;

   Class getDefaultBeanClass();
}
