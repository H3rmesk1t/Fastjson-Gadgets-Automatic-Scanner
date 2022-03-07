package com.sun.syndication.feed.impl;

import com.sun.syndication.feed.CopyFrom;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class CopyFromHelper {
   private static final Object[] NO_PARAMS = new Object[0];
   private Class _beanInterfaceClass;
   private Map _baseInterfaceMap;
   private Map _baseImplMap;
   private static final Set BASIC_TYPES = new HashSet();

   public CopyFromHelper(Class beanInterfaceClass, Map basePropInterfaceMap, Map basePropClassImplMap) {
      this._beanInterfaceClass = beanInterfaceClass;
      this._baseInterfaceMap = basePropInterfaceMap;
      this._baseImplMap = basePropClassImplMap;
   }

   public void copy(Object target, Object source) {
      try {
         PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(this._beanInterfaceClass);
         if (pds != null) {
            for(int i = 0; i < pds.length; ++i) {
               String propertyName = pds[i].getName();
               Method pReadMethod = pds[i].getReadMethod();
               Method pWriteMethod = pds[i].getWriteMethod();
               if (pReadMethod != null && pWriteMethod != null && pReadMethod.getDeclaringClass() != Object.class && pReadMethod.getParameterTypes().length == 0 && this._baseInterfaceMap.containsKey(propertyName)) {
                  Object value = pReadMethod.invoke(source, NO_PARAMS);
                  if (value != null) {
                     Class baseInterface = (Class)this._baseInterfaceMap.get(propertyName);
                     value = this.doCopy(value, baseInterface);
                     pWriteMethod.invoke(target, value);
                  }
               }
            }
         }

      } catch (Exception var10) {
         throw new RuntimeException("Could not do a copyFrom " + var10, var10);
      }
   }

   private CopyFrom createInstance(Class interfaceClass) throws Exception {
      return this._baseImplMap.get(interfaceClass) == null ? null : (CopyFrom)((Class)this._baseImplMap.get(interfaceClass)).newInstance();
   }

   private Object doCopy(Object value, Class baseInterface) throws Exception {
      if (value != null) {
         Class vClass = value.getClass();
         if (vClass.isArray()) {
            value = this.doCopyArray(value, baseInterface);
         } else if (value instanceof Collection) {
            value = this.doCopyCollection((Collection)value, baseInterface);
         } else if (value instanceof Map) {
            value = this.doCopyMap((Map)value, baseInterface);
         } else if (this.isBasicType(vClass)) {
            if (value instanceof Date) {
               value = ((Date)value).clone();
            }
         } else {
            if (!(value instanceof CopyFrom)) {
               throw new Exception("unsupported class for 'copyFrom' " + value.getClass());
            }

            CopyFrom source = (CopyFrom)value;
            CopyFrom target = this.createInstance(source.getInterface());
            target = target == null ? (CopyFrom)value.getClass().newInstance() : target;
            target.copyFrom(source);
            value = target;
         }
      }

      return value;
   }

   private Object doCopyArray(Object array, Class baseInterface) throws Exception {
      Class elementClass = array.getClass().getComponentType();
      int length = Array.getLength(array);
      Object newArray = Array.newInstance(elementClass, length);

      for(int i = 0; i < length; ++i) {
         Object element = this.doCopy(Array.get(array, i), baseInterface);
         Array.set(newArray, i, element);
      }

      return newArray;
   }

   private Object doCopyCollection(Collection collection, Class baseInterface) throws Exception {
      Collection newColl = collection instanceof Set ? new HashSet() : new ArrayList();
      Iterator i = collection.iterator();

      while(i.hasNext()) {
         Object element = this.doCopy(i.next(), baseInterface);
         ((Collection)newColl).add(element);
      }

      return newColl;
   }

   private Object doCopyMap(Map map, Class baseInterface) throws Exception {
      Map newMap = new HashMap();
      Iterator entries = map.entrySet().iterator();

      while(entries.hasNext()) {
         Entry entry = (Entry)entries.next();
         Object key = entry.getKey();
         Object element = this.doCopy(entry.getValue(), baseInterface);
         newMap.put(key, element);
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
      BASIC_TYPES.add(Date.class);
   }
}
