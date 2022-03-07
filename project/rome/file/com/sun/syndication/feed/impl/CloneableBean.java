package com.sun.syndication.feed.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class CloneableBean implements Serializable, Cloneable {
   private static final Class[] NO_PARAMS_DEF = new Class[0];
   private static final Object[] NO_PARAMS = new Object[0];
   private Object _obj;
   private Set _ignoreProperties;
   private static final Set BASIC_TYPES = new HashSet();
   private static final Map CONSTRUCTOR_BASIC_TYPES;

   protected CloneableBean() {
      this._obj = this;
   }

   public CloneableBean(Object obj) {
      this(obj, (Set)null);
   }

   public CloneableBean(Object obj, Set ignoreProperties) {
      this._obj = obj;
      this._ignoreProperties = ignoreProperties != null ? ignoreProperties : Collections.EMPTY_SET;
   }

   public Object clone() throws CloneNotSupportedException {
      return this.beanClone();
   }

   public Object beanClone() throws CloneNotSupportedException {
      try {
         Object clonedBean = this._obj.getClass().newInstance();
         PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(this._obj.getClass());
         if (pds != null) {
            for(int i = 0; i < pds.length; ++i) {
               Method pReadMethod = pds[i].getReadMethod();
               Method pWriteMethod = pds[i].getWriteMethod();
               if (pReadMethod != null && pWriteMethod != null && !this._ignoreProperties.contains(pds[i].getName()) && pReadMethod.getDeclaringClass() != Object.class && pReadMethod.getParameterTypes().length == 0) {
                  Object value = pReadMethod.invoke(this._obj, NO_PARAMS);
                  if (value != null) {
                     value = this.doClone(value);
                     pWriteMethod.invoke(clonedBean, value);
                  }
               }
            }
         }

         return clonedBean;
      } catch (CloneNotSupportedException var7) {
         throw var7;
      } catch (Exception var8) {
         System.out.println(var8);
         var8.printStackTrace(System.out);
         throw new CloneNotSupportedException("Cannot clone a " + this._obj.getClass() + " object");
      }
   }

   private Object doClone(Object value) throws Exception {
      if (value != null) {
         Class vClass = value.getClass();
         if (vClass.isArray()) {
            value = this.cloneArray(value);
         } else if (value instanceof Collection) {
            value = this.cloneCollection((Collection)value);
         } else if (value instanceof Map) {
            value = this.cloneMap((Map)value);
         } else if (!this.isBasicType(vClass)) {
            if (!(value instanceof Cloneable)) {
               throw new CloneNotSupportedException("Cannot clone a " + vClass.getName() + " object");
            }

            Method cloneMethod = vClass.getMethod("clone", NO_PARAMS_DEF);
            if (!Modifier.isPublic(cloneMethod.getModifiers())) {
               throw new CloneNotSupportedException("Cannot clone a " + value.getClass() + " object, clone() is not public");
            }

            value = cloneMethod.invoke(value, NO_PARAMS);
         }
      }

      return value;
   }

   private Object cloneArray(Object array) throws Exception {
      Class elementClass = array.getClass().getComponentType();
      int length = Array.getLength(array);
      Object newArray = Array.newInstance(elementClass, length);

      for(int i = 0; i < length; ++i) {
         Object element = this.doClone(Array.get(array, i));
         Array.set(newArray, i, element);
      }

      return newArray;
   }

   private Object cloneCollection(Collection collection) throws Exception {
      Class mClass = collection.getClass();
      Collection newColl = (Collection)mClass.newInstance();
      Iterator i = collection.iterator();

      while(i.hasNext()) {
         Object element = this.doClone(i.next());
         newColl.add(element);
      }

      return newColl;
   }

   private Object cloneMap(Map map) throws Exception {
      Class mClass = map.getClass();
      Map newMap = (Map)mClass.newInstance();
      Iterator entries = map.entrySet().iterator();

      while(entries.hasNext()) {
         Entry entry = (Entry)entries.next();
         Object key = this.doClone(entry.getKey());
         Object value = this.doClone(entry.getValue());
         newMap.put(key, value);
      }

      return newMap;
   }

   private boolean isBasicType(Class vClass) {
      return BASIC_TYPES.contains(vClass);
   }

   static {
      BASIC_TYPES.add(Boolean.class);
      BASIC_TYPES.add(Byte.class);
      BASIC_TYPES.add(Character.class);
      BASIC_TYPES.add(Double.class);
      BASIC_TYPES.add(Float.class);
      BASIC_TYPES.add(Integer.class);
      BASIC_TYPES.add(Long.class);
      BASIC_TYPES.add(Short.class);
      BASIC_TYPES.add(String.class);
      CONSTRUCTOR_BASIC_TYPES = new HashMap();
      CONSTRUCTOR_BASIC_TYPES.put(Boolean.class, new Class[]{Boolean.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(Byte.class, new Class[]{Byte.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(Character.class, new Class[]{Character.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(Double.class, new Class[]{Double.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(Float.class, new Class[]{Float.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(Integer.class, new Class[]{Integer.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(Long.class, new Class[]{Long.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(Short.class, new Class[]{Short.TYPE});
      CONSTRUCTOR_BASIC_TYPES.put(String.class, new Class[]{String.class});
   }
}
