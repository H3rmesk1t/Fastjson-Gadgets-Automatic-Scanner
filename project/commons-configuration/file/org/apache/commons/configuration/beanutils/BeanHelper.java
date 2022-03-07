package org.apache.commons.configuration.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.lang.ClassUtils;

public final class BeanHelper {
   private static final Map BEAN_FACTORIES = Collections.synchronizedMap(new HashMap());
   private static BeanFactory defaultBeanFactory;

   private BeanHelper() {
   }

   public static void registerBeanFactory(String name, BeanFactory factory) {
      if (name == null) {
         throw new IllegalArgumentException("Name for bean factory must not be null!");
      } else if (factory == null) {
         throw new IllegalArgumentException("Bean factory must not be null!");
      } else {
         BEAN_FACTORIES.put(name, factory);
      }
   }

   public static BeanFactory deregisterBeanFactory(String name) {
      return (BeanFactory)BEAN_FACTORIES.remove(name);
   }

   public static Set registeredFactoryNames() {
      return BEAN_FACTORIES.keySet();
   }

   public static BeanFactory getDefaultBeanFactory() {
      return defaultBeanFactory;
   }

   public static void setDefaultBeanFactory(BeanFactory factory) {
      if (factory == null) {
         throw new IllegalArgumentException("Default bean factory must not be null!");
      } else {
         defaultBeanFactory = factory;
      }
   }

   public static void initBean(Object bean, BeanDeclaration data) throws ConfigurationRuntimeException {
      initBeanProperties(bean, data);
      Map nestedBeans = data.getNestedBeanDeclarations();
      if (nestedBeans != null) {
         Entry e;
         String propName;
         Class defaultClass;
         if (bean instanceof Collection) {
            Collection coll = (Collection)bean;
            if (nestedBeans.size() == 1) {
               e = (Entry)nestedBeans.entrySet().iterator().next();
               propName = (String)e.getKey();
               defaultClass = getDefaultClass(bean, propName);
               if (e.getValue() instanceof List) {
                  List decls = (List)e.getValue();
                  Iterator i$ = decls.iterator();

                  while(i$.hasNext()) {
                     BeanDeclaration decl = (BeanDeclaration)i$.next();
                     coll.add(createBean(decl, defaultClass));
                  }
               } else {
                  BeanDeclaration decl = (BeanDeclaration)e.getValue();
                  coll.add(createBean(decl, defaultClass));
               }
            }
         } else {
            Iterator i$ = nestedBeans.entrySet().iterator();

            while(true) {
               while(i$.hasNext()) {
                  e = (Entry)i$.next();
                  propName = (String)e.getKey();
                  defaultClass = getDefaultClass(bean, propName);
                  Object prop = e.getValue();
                  if (prop instanceof Collection) {
                     Collection beanCollection = createPropertyCollection(propName, defaultClass);
                     Iterator i$ = ((Collection)prop).iterator();

                     while(i$.hasNext()) {
                        Object elemDef = i$.next();
                        beanCollection.add(createBean((BeanDeclaration)elemDef));
                     }

                     initProperty(bean, propName, beanCollection);
                  } else {
                     initProperty(bean, propName, createBean((BeanDeclaration)e.getValue(), defaultClass));
                  }
               }

               return;
            }
         }
      }

   }

   public static void initBeanProperties(Object bean, BeanDeclaration data) throws ConfigurationRuntimeException {
      Map properties = data.getBeanProperties();
      if (properties != null) {
         Iterator i$ = properties.entrySet().iterator();

         while(i$.hasNext()) {
            Entry e = (Entry)i$.next();
            String propName = (String)e.getKey();
            initProperty(bean, propName, e.getValue());
         }
      }

   }

   private static Class getDefaultClass(Object bean, String propName) {
      try {
         PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(bean, propName);
         return desc == null ? null : desc.getPropertyType();
      } catch (Exception var3) {
         return null;
      }
   }

   private static void initProperty(Object bean, String propName, Object value) throws ConfigurationRuntimeException {
      if (!PropertyUtils.isWriteable(bean, propName)) {
         throw new ConfigurationRuntimeException("Property " + propName + " cannot be set on " + bean.getClass().getName());
      } else {
         try {
            BeanUtils.setProperty(bean, propName, value);
         } catch (IllegalAccessException var4) {
            throw new ConfigurationRuntimeException(var4);
         } catch (InvocationTargetException var5) {
            throw new ConfigurationRuntimeException(var5);
         }
      }
   }

   private static Collection createPropertyCollection(String propName, Class propertyClass) {
      Collection beanCollection = null;
      if (List.class.isAssignableFrom(propertyClass)) {
         beanCollection = new ArrayList();
      } else {
         if (!Set.class.isAssignableFrom(propertyClass)) {
            throw new UnsupportedOperationException("Unable to handle collection of type : " + propertyClass.getName() + " for property " + propName);
         }

         beanCollection = new TreeSet();
      }

      return (Collection)beanCollection;
   }

   public static void setProperty(Object bean, String propName, Object value) {
      if (PropertyUtils.isWriteable(bean, propName)) {
         initProperty(bean, propName, value);
      }

   }

   public static Object createBean(BeanDeclaration data, Class defaultClass, Object param) throws ConfigurationRuntimeException {
      if (data == null) {
         throw new IllegalArgumentException("Bean declaration must not be null!");
      } else {
         BeanFactory factory = fetchBeanFactory(data);

         try {
            return factory.createBean(fetchBeanClass(data, defaultClass, factory), data, param);
         } catch (Exception var5) {
            throw new ConfigurationRuntimeException(var5);
         }
      }
   }

   public static Object createBean(BeanDeclaration data, Class defaultClass) throws ConfigurationRuntimeException {
      return createBean(data, defaultClass, (Object)null);
   }

   public static Object createBean(BeanDeclaration data) throws ConfigurationRuntimeException {
      return createBean(data, (Class)null);
   }

   static Class loadClass(String name, Class callingClass) throws ClassNotFoundException {
      return ClassUtils.getClass(name);
   }

   private static Class fetchBeanClass(BeanDeclaration data, Class defaultClass, BeanFactory factory) throws ConfigurationRuntimeException {
      String clsName = data.getBeanClassName();
      if (clsName != null) {
         try {
            return loadClass(clsName, factory.getClass());
         } catch (ClassNotFoundException var5) {
            throw new ConfigurationRuntimeException(var5);
         }
      } else if (defaultClass != null) {
         return defaultClass;
      } else {
         Class clazz = factory.getDefaultBeanClass();
         if (clazz == null) {
            throw new ConfigurationRuntimeException("Bean class is not specified!");
         } else {
            return clazz;
         }
      }
   }

   private static BeanFactory fetchBeanFactory(BeanDeclaration data) throws ConfigurationRuntimeException {
      String factoryName = data.getBeanFactoryName();
      if (factoryName != null) {
         BeanFactory factory = (BeanFactory)BEAN_FACTORIES.get(factoryName);
         if (factory == null) {
            throw new ConfigurationRuntimeException("Unknown bean factory: " + factoryName);
         } else {
            return factory;
         }
      } else {
         return getDefaultBeanFactory();
      }
   }

   static {
      defaultBeanFactory = DefaultBeanFactory.INSTANCE;
   }
}
