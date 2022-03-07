package org.apache.commons.configuration;

import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SystemConfiguration extends MapConfiguration {
   private static Log log = LogFactory.getLog(SystemConfiguration.class);

   public SystemConfiguration() {
      super(System.getProperties());
   }

   public static void setSystemProperties(String fileName) throws Exception {
      setSystemProperties((String)null, fileName);
   }

   public static void setSystemProperties(String basePath, String fileName) throws Exception {
      PropertiesConfiguration config = fileName.endsWith(".xml") ? new XMLPropertiesConfiguration() : new PropertiesConfiguration();
      if (basePath != null) {
         ((PropertiesConfiguration)config).setBasePath(basePath);
      }

      ((PropertiesConfiguration)config).setFileName(fileName);
      ((PropertiesConfiguration)config).load();
      setSystemProperties((PropertiesConfiguration)config);
   }

   public static void setSystemProperties(PropertiesConfiguration systemConfig) {
      String key;
      String value;
      for(Iterator iter = systemConfig.getKeys(); iter.hasNext(); System.setProperty(key, value)) {
         key = (String)iter.next();
         value = (String)systemConfig.getProperty(key);
         if (log.isDebugEnabled()) {
            log.debug("Setting system property " + key + " to " + value);
         }
      }

   }
}
