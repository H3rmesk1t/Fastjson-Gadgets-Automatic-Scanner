package com.sun.syndication.feed.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class EqualsBean implements Serializable {
   private static final Object[] NO_PARAMS = new Object[0];
   private Class _beanClass;
   private Object _obj;

   protected EqualsBean(Class beanClass) {
      this._beanClass = beanClass;
      this._obj = this;
   }

   public EqualsBean(Class beanClass, Object obj) {
      if (!beanClass.isInstance(obj)) {
         throw new IllegalArgumentException(obj.getClass() + " is not instance of " + beanClass);
      } else {
         this._beanClass = beanClass;
         this._obj = obj;
      }
   }

   public boolean equals(Object obj) {
      return this.beanEquals(obj);
   }

   public boolean beanEquals(Object obj) {
      Object bean1 = this._obj;
      Object bean2 = obj;
      boolean eq;
      if (obj == null) {
         eq = false;
      } else if (bean1 == null && obj == null) {
         eq = true;
      } else if (bean1 != null && obj != null) {
         if (!this._beanClass.isInstance(obj)) {
            eq = false;
         } else {
            eq = true;

            try {
               PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(this._beanClass);
               if (pds != null) {
                  for(int i = 0; eq && i < pds.length; ++i) {
                     Method pReadMethod = pds[i].getReadMethod();
                     if (pReadMethod != null && pReadMethod.getDeclaringClass() != Object.class && pReadMethod.getParameterTypes().length == 0) {
                        Object value1 = pReadMethod.invoke(bean1, NO_PARAMS);
                        Object value2 = pReadMethod.invoke(bean2, NO_PARAMS);
                        eq = this.doEquals(value1, value2);
                     }
                  }
               }
            } catch (Exception var10) {
               throw new RuntimeException("Could not execute equals()", var10);
            }
         }
      } else {
         eq = false;
      }

      return eq;
   }

   public int hashCode() {
      return this.beanHashCode();
   }

   public int beanHashCode() {
      return this._obj.toString().hashCode();
   }

   private boolean doEquals(Object obj1, Object obj2) {
      boolean eq = obj1 == obj2;
      if (!eq && obj1 != null && obj2 != null) {
         Class classObj1 = obj1.getClass();
         Class classObj2 = obj2.getClass();
         if (classObj1.isArray() && classObj2.isArray()) {
            eq = this.equalsArray(obj1, obj2);
         } else {
            eq = obj1.equals(obj2);
         }
      }

      return eq;
   }

   private boolean equalsArray(Object array1, Object array2) {
      int length1 = Array.getLength(array1);
      int length2 = Array.getLength(array2);
      boolean eq;
      if (length1 == length2) {
         eq = true;

         for(int i = 0; eq && i < length1; ++i) {
            Object e1 = Array.get(array1, i);
            Object e2 = Array.get(array2, i);
            eq = this.doEquals(e1, e2);
         }
      } else {
         eq = false;
      }

      return eq;
   }
}
