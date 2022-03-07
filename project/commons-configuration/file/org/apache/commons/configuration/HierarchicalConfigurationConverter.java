package org.apache.commons.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

abstract class HierarchicalConfigurationConverter {
   public void process(Configuration config) {
      if (config != null) {
         ConfigurationKey keyEmpty = new ConfigurationKey();
         ConfigurationKey keyLast = keyEmpty;
         Set keySet = new HashSet();
         Iterator it = config.getKeys();

         while(it.hasNext()) {
            String key = (String)it.next();
            if (!keySet.contains(key)) {
               ConfigurationKey keyAct = new ConfigurationKey(key);
               this.closeElements(keyLast, keyAct);
               String elem = this.openElements(keyLast, keyAct, config, keySet);
               this.fireValue(elem, config.getProperty(key));
               keyLast = keyAct;
            }
         }

         this.closeElements(keyLast, keyEmpty);
      }

   }

   protected abstract void elementStart(String var1, Object var2);

   protected abstract void elementEnd(String var1);

   protected void closeElements(ConfigurationKey keyLast, ConfigurationKey keyAct) {
      ConfigurationKey keyDiff = keyAct.differenceKey(keyLast);
      Iterator it = this.reverseIterator(keyDiff);
      if (it.hasNext()) {
         it.next();
      }

      while(it.hasNext()) {
         this.elementEnd((String)it.next());
      }

   }

   protected Iterator reverseIterator(ConfigurationKey key) {
      List list = new ArrayList();
      ConfigurationKey.KeyIterator it = key.iterator();

      while(it.hasNext()) {
         list.add(it.nextKey());
      }

      Collections.reverse(list);
      return list.iterator();
   }

   protected String openElements(ConfigurationKey keyLast, ConfigurationKey keyAct, Configuration config, Set keySet) {
      ConfigurationKey.KeyIterator it = keyLast.differenceKey(keyAct).iterator();
      ConfigurationKey k = keyLast.commonKey(keyAct);
      it.nextKey();

      while(it.hasNext()) {
         k.append(it.currentKey(true));
         this.elementStart(it.currentKey(true), config.getProperty(k.toString()));
         keySet.add(k.toString());
         it.nextKey();
      }

      return it.currentKey(true);
   }

   protected void fireValue(String name, Object value) {
      if (value instanceof Collection) {
         Collection valueCol = (Collection)value;
         Iterator i$ = valueCol.iterator();

         while(i$.hasNext()) {
            Object v = i$.next();
            this.fireValue(name, v);
         }
      } else {
         this.elementStart(name, value);
         this.elementEnd(name);
      }

   }
}
