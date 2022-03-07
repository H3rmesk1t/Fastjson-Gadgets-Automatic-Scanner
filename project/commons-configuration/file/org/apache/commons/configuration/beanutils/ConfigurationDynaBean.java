package org.apache.commons.configuration.beanutils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationMap;
import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationDynaBean extends ConfigurationMap implements DynaBean {
   private static final String PROPERTY_DELIMITER = ".";
   private static final Log LOG = LogFactory.getLog(ConfigurationDynaBean.class);

   public ConfigurationDynaBean(Configuration configuration) {
      super(configuration);
      if (LOG.isTraceEnabled()) {
         LOG.trace("ConfigurationDynaBean(" + configuration + ")");
      }

   }

   public void set(String name, Object value) {
      if (LOG.isTraceEnabled()) {
         LOG.trace("set(" + name + "," + value + ")");
      }

      if (value == null) {
         throw new NullPointerException("Error trying to set property to null.");
      } else {
         if (value instanceof Collection) {
            Collection collection = (Collection)value;
            Iterator i$ = collection.iterator();

            while(i$.hasNext()) {
               Object v = i$.next();
               this.getConfiguration().addProperty(name, v);
            }
         } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);

            for(int i = 0; i < length; ++i) {
               this.getConfiguration().addProperty(name, Array.get(value, i));
            }
         } else {
            this.getConfiguration().setProperty(name, value);
         }

      }
   }

   public Object get(String name) {
      if (LOG.isTraceEnabled()) {
         LOG.trace("get(" + name + ")");
      }

      Object result = this.getConfiguration().getProperty(name);
      if (result == null) {
         Configuration subset = new SubsetConfiguration(this.getConfiguration(), name, ".");
         if (!subset.isEmpty()) {
            result = new ConfigurationDynaBean(subset);
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug(name + "=[" + result + "]");
      }

      if (result == null) {
         throw new IllegalArgumentException("Property '" + name + "' does not exist.");
      } else {
         return result;
      }
   }

   public boolean contains(String name, String key) {
      Configuration subset = this.getConfiguration().subset(name);
      if (subset == null) {
         throw new IllegalArgumentException("Mapped property '" + name + "' does not exist.");
      } else {
         return subset.containsKey(key);
      }
   }

   public Object get(String name, int index) {
      if (!this.checkIndexedProperty(name)) {
         throw new IllegalArgumentException("Property '" + name + "' is not indexed.");
      } else {
         List list = this.getConfiguration().getList(name);
         return list.get(index);
      }
   }

   public Object get(String name, String key) {
      Configuration subset = this.getConfiguration().subset(name);
      if (subset == null) {
         throw new IllegalArgumentException("Mapped property '" + name + "' does not exist.");
      } else {
         return subset.getProperty(key);
      }
   }

   public DynaClass getDynaClass() {
      return new ConfigurationDynaClass(this.getConfiguration());
   }

   public void remove(String name, String key) {
      Configuration subset = new SubsetConfiguration(this.getConfiguration(), name, ".");
      subset.setProperty(key, (Object)null);
   }

   public void set(String name, int index, Object value) {
      if (!this.checkIndexedProperty(name) && index > 0) {
         throw new IllegalArgumentException("Property '" + name + "' is not indexed.");
      } else {
         Object property = this.getConfiguration().getProperty(name);
         if (property instanceof List) {
            List list = (List)property;
            list.set(index, value);
            this.getConfiguration().setProperty(name, list);
         } else if (property.getClass().isArray()) {
            Array.set(property, index, value);
         } else if (index == 0) {
            this.getConfiguration().setProperty(name, value);
         }

      }
   }

   public void set(String name, String key, Object value) {
      this.getConfiguration().setProperty(name + "." + key, value);
   }

   private boolean checkIndexedProperty(String name) {
      Object property = this.getConfiguration().getProperty(name);
      if (property == null) {
         throw new IllegalArgumentException("Property '" + name + "' does not exist.");
      } else {
         return property instanceof List || property.getClass().isArray();
      }
   }
}
