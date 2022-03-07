package org.apache.commons.configuration.interpol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.text.StrLookup;

public class ConfigurationInterpolator extends StrLookup {
   public static final String PREFIX_SYSPROPERTIES = "sys";
   public static final String PREFIX_CONSTANTS = "const";
   public static final String PREFIX_ENVIRONMENT = "env";
   private static final char PREFIX_SEPARATOR = ':';
   private static Map globalLookups = new HashMap();
   private Map localLookups;
   private StrLookup defaultLookup;
   private ConfigurationInterpolator parentInterpolator;

   public ConfigurationInterpolator() {
      synchronized(globalLookups) {
         this.localLookups = new HashMap(globalLookups);
      }
   }

   public static void registerGlobalLookup(String prefix, StrLookup lookup) {
      if (prefix == null) {
         throw new IllegalArgumentException("Prefix for lookup object must not be null!");
      } else if (lookup == null) {
         throw new IllegalArgumentException("Lookup object must not be null!");
      } else {
         synchronized(globalLookups) {
            globalLookups.put(prefix, lookup);
         }
      }
   }

   public static boolean deregisterGlobalLookup(String prefix) {
      synchronized(globalLookups) {
         return globalLookups.remove(prefix) != null;
      }
   }

   public void registerLookup(String prefix, StrLookup lookup) {
      if (prefix == null) {
         throw new IllegalArgumentException("Prefix for lookup object must not be null!");
      } else if (lookup == null) {
         throw new IllegalArgumentException("Lookup object must not be null!");
      } else {
         this.localLookups.put(prefix, lookup);
      }
   }

   public boolean deregisterLookup(String prefix) {
      return this.localLookups.remove(prefix) != null;
   }

   public Set prefixSet() {
      return this.localLookups.keySet();
   }

   public StrLookup getDefaultLookup() {
      return this.defaultLookup;
   }

   public void setDefaultLookup(StrLookup defaultLookup) {
      this.defaultLookup = defaultLookup;
   }

   public String lookup(String var) {
      if (var == null) {
         return null;
      } else {
         int prefixPos = var.indexOf(58);
         String value;
         if (prefixPos >= 0) {
            value = var.substring(0, prefixPos);
            String name = var.substring(prefixPos + 1);
            String value = this.fetchLookupForPrefix(value).lookup(name);
            if (value == null && this.getParentInterpolator() != null) {
               value = this.getParentInterpolator().lookup(name);
            }

            if (value != null) {
               return value;
            }
         }

         value = this.fetchNoPrefixLookup().lookup(var);
         if (value == null && this.getParentInterpolator() != null) {
            value = this.getParentInterpolator().lookup(var);
         }

         return value;
      }
   }

   protected StrLookup fetchNoPrefixLookup() {
      return this.getDefaultLookup() != null ? this.getDefaultLookup() : StrLookup.noneLookup();
   }

   protected StrLookup fetchLookupForPrefix(String prefix) {
      StrLookup lookup = (StrLookup)this.localLookups.get(prefix);
      if (lookup == null) {
         lookup = StrLookup.noneLookup();
      }

      return lookup;
   }

   public void registerLocalLookups(ConfigurationInterpolator interpolator) {
      interpolator.localLookups.putAll(this.localLookups);
   }

   public void setParentInterpolator(ConfigurationInterpolator parentInterpolator) {
      this.parentInterpolator = parentInterpolator;
   }

   public ConfigurationInterpolator getParentInterpolator() {
      return this.parentInterpolator;
   }

   static {
      globalLookups.put("sys", StrLookup.systemPropertiesLookup());
      globalLookups.put("const", new ConstantLookup());
      globalLookups.put("env", new EnvironmentLookup());
   }
}
