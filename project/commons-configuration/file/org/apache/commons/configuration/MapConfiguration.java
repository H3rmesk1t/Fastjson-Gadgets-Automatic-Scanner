package org.apache.commons.configuration;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class MapConfiguration extends AbstractConfiguration implements Cloneable {
   protected Map map;
   private boolean trimmingDisabled;

   public MapConfiguration(Map map) {
      this.map = map;
   }

   public MapConfiguration(Properties props) {
      this.map = convertPropertiesToMap(props);
   }

   public Map getMap() {
      return this.map;
   }

   public boolean isTrimmingDisabled() {
      return this.trimmingDisabled;
   }

   public void setTrimmingDisabled(boolean trimmingDisabled) {
      this.trimmingDisabled = trimmingDisabled;
   }

   public Object getProperty(String key) {
      Object value = this.map.get(key);
      if (value instanceof String && !this.isDelimiterParsingDisabled()) {
         List list = PropertyConverter.split((String)value, this.getListDelimiter(), !this.isTrimmingDisabled());
         return list.size() > 1 ? list : list.get(0);
      } else {
         return value;
      }
   }

   protected void addPropertyDirect(String key, Object value) {
      Object previousValue = this.getProperty(key);
      if (previousValue == null) {
         this.map.put(key, value);
      } else if (previousValue instanceof List) {
         ((List)previousValue).add(value);
      } else {
         List list = new ArrayList();
         list.add(previousValue);
         list.add(value);
         this.map.put(key, list);
      }

   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean containsKey(String key) {
      return this.map.containsKey(key);
   }

   protected void clearPropertyDirect(String key) {
      this.map.remove(key);
   }

   public Iterator getKeys() {
      return this.map.keySet().iterator();
   }

   public Object clone() {
      try {
         MapConfiguration copy = (MapConfiguration)super.clone();
         copy.clearConfigurationListeners();
         Map clonedMap = (Map)ConfigurationUtils.clone(this.map);
         copy.map = clonedMap;
         return copy;
      } catch (CloneNotSupportedException var3) {
         throw new ConfigurationRuntimeException(var3);
      }
   }

   private static Map convertPropertiesToMap(final Properties props) {
      return new AbstractMap() {
         public Set entrySet() {
            Set entries = new HashSet();
            Iterator i$ = props.entrySet().iterator();

            while(i$.hasNext()) {
               final Entry propertyEntry = (Entry)i$.next();
               if (propertyEntry.getKey() instanceof String) {
                  entries.add(new Entry() {
                     public String getKey() {
                        return propertyEntry.getKey().toString();
                     }

                     public Object getValue() {
                        return propertyEntry.getValue();
                     }

                     public Object setValue(Object value) {
                        throw new UnsupportedOperationException();
                     }
                  });
               }
            }

            return entries;
         }
      };
   }
}
