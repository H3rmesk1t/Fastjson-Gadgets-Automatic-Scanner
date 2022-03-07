package com.sun.syndication.feed.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanIntrospector {
   private static final Map _introspected = new HashMap();
   private static final String SETTER = "set";
   private static final String GETTER = "get";
   private static final String BOOLEAN_GETTER = "is";

   public static synchronized PropertyDescriptor[] getPropertyDescriptors(Class klass) throws IntrospectionException {
      PropertyDescriptor[] descriptors = (PropertyDescriptor[])((PropertyDescriptor[])_introspected.get(klass));
      if (descriptors == null) {
         descriptors = getPDs(klass);
         _introspected.put(klass, descriptors);
      }

      return descriptors;
   }

   private static PropertyDescriptor[] getPDs(Class klass) throws IntrospectionException {
      Method[] methods = klass.getMethods();
      Map getters = getPDs(methods, false);
      Map setters = getPDs(methods, true);
      List pds = merge(getters, setters);
      PropertyDescriptor[] array = new PropertyDescriptor[pds.size()];
      pds.toArray(array);
      return array;
   }

   private static Map getPDs(Method[] methods, boolean setters) throws IntrospectionException {
      Map pds = new HashMap();

      for(int i = 0; i < methods.length; ++i) {
         String pName = null;
         PropertyDescriptor pDescriptor = null;
         if ((methods[i].getModifiers() & 1) != 0) {
            if (setters) {
               if (methods[i].getName().startsWith("set") && methods[i].getReturnType() == Void.TYPE && methods[i].getParameterTypes().length == 1) {
                  pName = Introspector.decapitalize(methods[i].getName().substring(3));
                  pDescriptor = new PropertyDescriptor(pName, (Method)null, methods[i]);
               }
            } else if (methods[i].getName().startsWith("get") && methods[i].getReturnType() != Void.TYPE && methods[i].getParameterTypes().length == 0) {
               pName = Introspector.decapitalize(methods[i].getName().substring(3));
               pDescriptor = new PropertyDescriptor(pName, methods[i], (Method)null);
            } else if (methods[i].getName().startsWith("is") && methods[i].getReturnType() == Boolean.TYPE && methods[i].getParameterTypes().length == 0) {
               pName = Introspector.decapitalize(methods[i].getName().substring(2));
               pDescriptor = new PropertyDescriptor(pName, methods[i], (Method)null);
            }
         }

         if (pName != null) {
            pds.put(pName, pDescriptor);
         }
      }

      return pds;
   }

   private static List merge(Map getters, Map setters) throws IntrospectionException {
      List props = new ArrayList();
      Set processedProps = new HashSet();
      Iterator gs = getters.keySet().iterator();

      PropertyDescriptor setter;
      while(gs.hasNext()) {
         String name = (String)gs.next();
         PropertyDescriptor getter = (PropertyDescriptor)getters.get(name);
         PropertyDescriptor setter = (PropertyDescriptor)setters.get(name);
         if (setter != null) {
            processedProps.add(name);
            setter = new PropertyDescriptor(name, getter.getReadMethod(), setter.getWriteMethod());
            props.add(setter);
         } else {
            props.add(getter);
         }
      }

      Set writeOnlyProps = new HashSet(setters.keySet());
      writeOnlyProps.removeAll(processedProps);
      Iterator ss = writeOnlyProps.iterator();

      while(ss.hasNext()) {
         String name = (String)ss.next();
         setter = (PropertyDescriptor)setters.get(name);
         props.add(setter);
      }

      return props;
   }
}
