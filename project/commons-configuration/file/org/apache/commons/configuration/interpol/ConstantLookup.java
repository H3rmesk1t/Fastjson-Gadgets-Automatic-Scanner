package org.apache.commons.configuration.interpol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConstantLookup extends StrLookup {
   private static final char FIELD_SEPRATOR = '.';
   private static Map constantCache = new HashMap();
   private Log log = LogFactory.getLog(this.getClass());

   public String lookup(String var) {
      if (var == null) {
         return null;
      } else {
         String result;
         synchronized(constantCache) {
            result = (String)constantCache.get(var);
         }

         if (result != null) {
            return result;
         } else {
            int fieldPos = var.lastIndexOf(46);
            if (fieldPos < 0) {
               return null;
            } else {
               try {
                  Object value = this.resolveField(var.substring(0, fieldPos), var.substring(fieldPos + 1));
                  if (value != null) {
                     synchronized(constantCache) {
                        constantCache.put(var, String.valueOf(value));
                     }

                     result = value.toString();
                  }
               } catch (Exception var8) {
                  this.log.warn("Could not obtain value for variable " + var, var8);
               }

               return result;
            }
         }
      }
   }

   public static void clear() {
      synchronized(constantCache) {
         constantCache.clear();
      }
   }

   protected Object resolveField(String className, String fieldName) throws Exception {
      Class clazz = this.fetchClass(className);
      Field field = clazz.getField(fieldName);
      return field.get((Object)null);
   }

   protected Class fetchClass(String className) throws ClassNotFoundException {
      return ClassUtils.getClass(className);
   }
}
