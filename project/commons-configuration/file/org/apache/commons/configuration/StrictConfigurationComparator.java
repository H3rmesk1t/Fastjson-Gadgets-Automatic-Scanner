package org.apache.commons.configuration;

import java.util.Iterator;

public class StrictConfigurationComparator implements ConfigurationComparator {
   public boolean compare(Configuration a, Configuration b) {
      if (a == null && b == null) {
         return true;
      } else if (a != null && b != null) {
         Iterator keys = a.getKeys();

         String key;
         Object value;
         do {
            if (!keys.hasNext()) {
               keys = b.getKeys();

               do {
                  if (!keys.hasNext()) {
                     return true;
                  }

                  key = (String)keys.next();
                  value = b.getProperty(key);
               } while(value.equals(a.getProperty(key)));

               return false;
            }

            key = (String)keys.next();
            value = a.getProperty(key);
         } while(value.equals(b.getProperty(key)));

         return false;
      } else {
         return false;
      }
   }
}
