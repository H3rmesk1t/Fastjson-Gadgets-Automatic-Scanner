package org.apache.commons.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BaseConfiguration extends AbstractConfiguration implements Cloneable {
   private Map store = new LinkedHashMap();

   protected void addPropertyDirect(String key, Object value) {
      Object previousValue = this.getProperty(key);
      if (previousValue == null) {
         this.store.put(key, value);
      } else if (previousValue instanceof List) {
         List valueList = (List)previousValue;
         valueList.add(value);
      } else {
         List list = new ArrayList();
         list.add(previousValue);
         list.add(value);
         this.store.put(key, list);
      }

   }

   public Object getProperty(String key) {
      return this.store.get(key);
   }

   public boolean isEmpty() {
      return this.store.isEmpty();
   }

   public boolean containsKey(String key) {
      return this.store.containsKey(key);
   }

   protected void clearPropertyDirect(String key) {
      if (this.containsKey(key)) {
         this.store.remove(key);
      }

   }

   public void clear() {
      this.fireEvent(4, (String)null, (Object)null, true);
      this.store.clear();
      this.fireEvent(4, (String)null, (Object)null, false);
   }

   public Iterator getKeys() {
      return this.store.keySet().iterator();
   }

   public Object clone() {
      try {
         BaseConfiguration copy = (BaseConfiguration)super.clone();
         Map clonedStore = (Map)ConfigurationUtils.clone(this.store);
         copy.store = clonedStore;
         Iterator i$ = this.store.entrySet().iterator();

         while(i$.hasNext()) {
            Entry e = (Entry)i$.next();
            if (e.getValue() instanceof Collection) {
               Collection strList = (Collection)e.getValue();
               copy.store.put(e.getKey(), new ArrayList(strList));
            }
         }

         return copy;
      } catch (CloneNotSupportedException var6) {
         throw new ConfigurationRuntimeException(var6);
      }
   }
}
