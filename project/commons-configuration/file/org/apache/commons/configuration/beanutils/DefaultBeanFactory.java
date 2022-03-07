package org.apache.commons.configuration.beanutils;

public class DefaultBeanFactory implements BeanFactory {
   public static final DefaultBeanFactory INSTANCE = new DefaultBeanFactory();

   public Object createBean(Class beanClass, BeanDeclaration data, Object parameter) throws Exception {
      Object result = this.createBeanInstance(beanClass, data);
      this.initBeanInstance(result, data);
      return result;
   }

   public Class getDefaultBeanClass() {
      return null;
   }

   protected Object createBeanInstance(Class beanClass, BeanDeclaration data) throws Exception {
      return beanClass.newInstance();
   }

   protected void initBeanInstance(Object bean, BeanDeclaration data) throws Exception {
      BeanHelper.initBean(bean, data);
   }
}
