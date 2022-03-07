package org.apache.commons.configuration.beanutils;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationDynaClass implements DynaClass {
   private static final Log LOG = LogFactory.getLog(ConfigurationDynaClass.class);
   private final Configuration configuration;

   public ConfigurationDynaClass(Configuration configuration) {
      if (LOG.isTraceEnabled()) {
         LOG.trace("ConfigurationDynaClass(" + configuration + ")");
      }

      this.configuration = configuration;
   }

   public DynaProperty getDynaProperty(String name) {
      if (LOG.isTraceEnabled()) {
         LOG.trace("getDynaProperty(" + name + ")");
      }

      if (name == null) {
         throw new IllegalArgumentException("Property name must not be null!");
      } else {
         Object value = this.configuration.getProperty(name);
         if (value == null) {
            return null;
         } else {
            Class type = value.getClass();
            if (type == Byte.class) {
               type = Byte.TYPE;
            }

            if (type == Character.class) {
               type = Character.TYPE;
            } else if (type == Boolean.class) {
               type = Boolean.TYPE;
            } else if (type == Double.class) {
               type = Double.TYPE;
            } else if (type == Float.class) {
               type = Float.TYPE;
            } else if (type == Integer.class) {
               type = Integer.TYPE;
            } else if (type == Long.class) {
               type = Long.TYPE;
            } else if (type == Short.class) {
               type = Short.TYPE;
            }

            return new DynaProperty(name, type);
         }
      }
   }

   public DynaProperty[] getDynaProperties() {
      if (LOG.isTraceEnabled()) {
         LOG.trace("getDynaProperties()");
      }

      Iterator keys = this.configuration.getKeys();
      ArrayList properties = new ArrayList();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         DynaProperty property = this.getDynaProperty(key);
         properties.add(property);
      }

      DynaProperty[] propertyArray = new DynaProperty[properties.size()];
      properties.toArray(propertyArray);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Found " + properties.size() + " properties.");
      }

      return propertyArray;
   }

   public String getName() {
      return ConfigurationDynaBean.class.getName();
   }

   public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
      return new ConfigurationDynaBean(this.configuration);
   }
}
