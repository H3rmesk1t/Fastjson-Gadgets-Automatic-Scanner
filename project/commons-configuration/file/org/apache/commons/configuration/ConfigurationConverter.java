package org.apache.commons.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringUtils;

public final class ConfigurationConverter {
   private ConfigurationConverter() {
   }

   public static Configuration getConfiguration(ExtendedProperties eprops) {
      return new MapConfiguration(eprops);
   }

   public static Configuration getConfiguration(Properties props) {
      return new MapConfiguration(props);
   }

   public static ExtendedProperties getExtendedProperties(Configuration config) {
      ExtendedProperties props = new ExtendedProperties();

      String key;
      Object property;
      for(Iterator keys = config.getKeys(); keys.hasNext(); props.setProperty(key, property)) {
         key = (String)keys.next();
         property = config.getProperty(key);
         if (property instanceof List) {
            property = new ArrayList((List)property);
         }
      }

      return props;
   }

   public static Properties getProperties(Configuration config) {
      Properties props = new Properties();
      char delimiter = config instanceof AbstractConfiguration ? ((AbstractConfiguration)config).getListDelimiter() : 44;
      Iterator keys = config.getKeys();

      while(keys.hasNext()) {
         String key = (String)keys.next();
         List list = config.getList(key);
         props.setProperty(key, StringUtils.join(list.iterator(), delimiter));
      }

      return props;
   }

   public static Map getMap(Configuration config) {
      return new ConfigurationMap(config);
   }
}
