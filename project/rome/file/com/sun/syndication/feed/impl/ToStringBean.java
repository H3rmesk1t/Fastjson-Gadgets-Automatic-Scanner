package com.sun.syndication.feed.impl;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

public class ToStringBean implements Serializable {
   private static final ThreadLocal PREFIX_TL = new ThreadLocal() {
      public Object get() {
         Object o = super.get();
         if (o == null) {
            o = new Stack();
            this.set(o);
         }

         return o;
      }
   };
   private static final Object[] NO_PARAMS = new Object[0];
   private Class _beanClass;
   private Object _obj;

   protected ToStringBean(Class beanClass) {
      this._beanClass = beanClass;
      this._obj = this;
   }

   public ToStringBean(Class beanClass, Object obj) {
      this._beanClass = beanClass;
      this._obj = obj;
   }

   public String toString() {
      Stack stack = (Stack)PREFIX_TL.get();
      String[] tsInfo = (String[])(stack.isEmpty() ? null : stack.peek());
      String prefix;
      if (tsInfo == null) {
         String className = this._obj.getClass().getName();
         prefix = className.substring(className.lastIndexOf(".") + 1);
      } else {
         prefix = tsInfo[0];
         tsInfo[1] = prefix;
      }

      return this.toString(prefix);
   }

   private String toString(String prefix) {
      StringBuffer sb = new StringBuffer(128);

      try {
         PropertyDescriptor[] pds = BeanIntrospector.getPropertyDescriptors(this._beanClass);
         if (pds != null) {
            for(int i = 0; i < pds.length; ++i) {
               String pName = pds[i].getName();
               Method pReadMethod = pds[i].getReadMethod();
               if (pReadMethod != null && pReadMethod.getDeclaringClass() != Object.class && pReadMethod.getParameterTypes().length == 0) {
                  Object value = pReadMethod.invoke(this._obj, NO_PARAMS);
                  this.printProperty(sb, prefix + "." + pName, value);
               }
            }
         }
      } catch (Exception var8) {
         sb.append("\n\nEXCEPTION: Could not complete " + this._obj.getClass() + ".toString(): " + var8.getMessage() + "\n");
      }

      return sb.toString();
   }

   private void printProperty(StringBuffer sb, String prefix, Object value) {
      if (value == null) {
         sb.append(prefix).append("=null\n");
      } else if (value.getClass().isArray()) {
         this.printArrayProperty(sb, prefix, value);
      } else {
         Iterator i;
         String cPrefix;
         Object cValue;
         String[] tsInfo;
         Stack stack;
         String s;
         if (value instanceof Map) {
            Map map = (Map)value;
            i = map.entrySet().iterator();
            if (i.hasNext()) {
               while(i.hasNext()) {
                  Entry me = (Entry)i.next();
                  cPrefix = prefix + "[" + me.getKey() + "]";
                  cValue = me.getValue();
                  tsInfo = new String[]{cPrefix, null};
                  stack = (Stack)PREFIX_TL.get();
                  stack.push(tsInfo);
                  s = cValue != null ? cValue.toString() : "null";
                  stack.pop();
                  if (tsInfo[1] == null) {
                     sb.append(cPrefix).append("=").append(s).append("\n");
                  } else {
                     sb.append(s);
                  }
               }
            } else {
               sb.append(prefix).append("=[]\n");
            }
         } else if (value instanceof Collection) {
            Collection collection = (Collection)value;
            i = collection.iterator();
            if (i.hasNext()) {
               int var15 = 0;

               while(i.hasNext()) {
                  cPrefix = prefix + "[" + var15++ + "]";
                  cValue = i.next();
                  tsInfo = new String[]{cPrefix, null};
                  stack = (Stack)PREFIX_TL.get();
                  stack.push(tsInfo);
                  s = cValue != null ? cValue.toString() : "null";
                  stack.pop();
                  if (tsInfo[1] == null) {
                     sb.append(cPrefix).append("=").append(s).append("\n");
                  } else {
                     sb.append(s);
                  }
               }
            } else {
               sb.append(prefix).append("=[]\n");
            }
         } else {
            String[] tsInfo = new String[]{prefix, null};
            Stack stack = (Stack)PREFIX_TL.get();
            stack.push(tsInfo);
            String s = value.toString();
            stack.pop();
            if (tsInfo[1] == null) {
               sb.append(prefix).append("=").append(s).append("\n");
            } else {
               sb.append(s);
            }
         }
      }

   }

   private void printArrayProperty(StringBuffer sb, String prefix, Object array) {
      int length = Array.getLength(array);

      for(int i = 0; i < length; ++i) {
         Object obj = Array.get(array, i);
         this.printProperty(sb, prefix + "[" + i + "]", obj);
      }

   }
}
