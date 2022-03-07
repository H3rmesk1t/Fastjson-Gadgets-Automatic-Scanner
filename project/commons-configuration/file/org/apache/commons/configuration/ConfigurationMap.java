package org.apache.commons.configuration;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class ConfigurationMap extends AbstractMap {
   private final Configuration configuration;

   public ConfigurationMap(Configuration configuration) {
      this.configuration = configuration;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public Set entrySet() {
      return new ConfigurationMap.ConfigurationSet(this.configuration);
   }

   public Object put(Object key, Object value) {
      String strKey = String.valueOf(key);
      Object old = this.configuration.getProperty(strKey);
      this.configuration.setProperty(strKey, value);
      return old;
   }

   public Object get(Object key) {
      return this.configuration.getProperty(String.valueOf(key));
   }

   static class ConfigurationSet extends AbstractSet {
      private final Configuration configuration;

      ConfigurationSet(Configuration configuration) {
         this.configuration = configuration;
      }

      public int size() {
         int count = 0;

         for(Iterator iterator = this.configuration.getKeys(); iterator.hasNext(); ++count) {
            iterator.next();
         }

         return count;
      }

      public Iterator iterator() {
         return new ConfigurationMap.ConfigurationSet.ConfigurationSetIterator();
      }

      private final class ConfigurationSetIterator implements Iterator {
         private final Iterator keys;

         private ConfigurationSetIterator() {
            this.keys = ConfigurationSet.this.configuration.getKeys();
         }

         public boolean hasNext() {
            return this.keys.hasNext();
         }

         public java.util.Map.Entry next() {
            return ConfigurationSet.this.new Entry(this.keys.next());
         }

         public void remove() {
            this.keys.remove();
         }

         // $FF: synthetic method
         ConfigurationSetIterator(Object x1) {
            this();
         }
      }

      private final class Entry implements java.util.Map.Entry {
         private Object key;

         private Entry(Object key) {
            this.key = key;
         }

         public Object getKey() {
            return this.key;
         }

         public Object getValue() {
            return ConfigurationSet.this.configuration.getProperty((String)this.key);
         }

         public Object setValue(Object value) {
            Object old = this.getValue();
            ConfigurationSet.this.configuration.setProperty((String)this.key, value);
            return old;
         }

         // $FF: synthetic method
         Entry(Object x1, Object x2) {
            this(x1);
         }
      }
   }
}
