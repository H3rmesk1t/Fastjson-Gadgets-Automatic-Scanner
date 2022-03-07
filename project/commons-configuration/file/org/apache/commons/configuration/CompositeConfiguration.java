package org.apache.commons.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class CompositeConfiguration extends AbstractConfiguration implements Cloneable {
   private List configList;
   private Configuration inMemoryConfiguration;
   private boolean inMemoryConfigIsChild;

   public CompositeConfiguration() {
      this.configList = new LinkedList();
      this.clear();
   }

   public CompositeConfiguration(Configuration inMemoryConfiguration) {
      this.configList = new LinkedList();
      this.configList.clear();
      this.inMemoryConfiguration = inMemoryConfiguration;
      this.configList.add(inMemoryConfiguration);
   }

   public CompositeConfiguration(Collection configurations) {
      this(new BaseConfiguration(), configurations);
   }

   public CompositeConfiguration(Configuration inMemoryConfiguration, Collection configurations) {
      this(inMemoryConfiguration);
      if (configurations != null) {
         Iterator i$ = configurations.iterator();

         while(i$.hasNext()) {
            Configuration c = (Configuration)i$.next();
            this.addConfiguration(c);
         }
      }

   }

   public void addConfiguration(Configuration config) {
      this.addConfiguration(config, false);
   }

   public void addConfiguration(Configuration config, boolean asInMemory) {
      if (!this.configList.contains(config)) {
         if (asInMemory) {
            this.replaceInMemoryConfiguration(config);
            this.inMemoryConfigIsChild = true;
         }

         if (!this.inMemoryConfigIsChild) {
            this.configList.add(this.configList.indexOf(this.inMemoryConfiguration), config);
         } else {
            this.configList.add(config);
         }

         if (config instanceof AbstractConfiguration) {
            ((AbstractConfiguration)config).setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
         }
      }

   }

   public void removeConfiguration(Configuration config) {
      if (!config.equals(this.inMemoryConfiguration)) {
         this.configList.remove(config);
      }

   }

   public int getNumberOfConfigurations() {
      return this.configList.size();
   }

   public void clear() {
      this.configList.clear();
      this.inMemoryConfiguration = new BaseConfiguration();
      ((BaseConfiguration)this.inMemoryConfiguration).setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
      ((BaseConfiguration)this.inMemoryConfiguration).setListDelimiter(this.getListDelimiter());
      ((BaseConfiguration)this.inMemoryConfiguration).setDelimiterParsingDisabled(this.isDelimiterParsingDisabled());
      this.configList.add(this.inMemoryConfiguration);
      this.inMemoryConfigIsChild = false;
   }

   protected void addPropertyDirect(String key, Object token) {
      this.inMemoryConfiguration.addProperty(key, token);
   }

   public Object getProperty(String key) {
      Configuration firstMatchingConfiguration = null;
      Iterator i$ = this.configList.iterator();

      while(i$.hasNext()) {
         Configuration config = (Configuration)i$.next();
         if (config.containsKey(key)) {
            firstMatchingConfiguration = config;
            break;
         }
      }

      return firstMatchingConfiguration != null ? firstMatchingConfiguration.getProperty(key) : null;
   }

   public Iterator getKeys() {
      Set keys = new LinkedHashSet();
      Iterator i$ = this.configList.iterator();

      while(i$.hasNext()) {
         Configuration config = (Configuration)i$.next();
         Iterator it = config.getKeys();

         while(it.hasNext()) {
            keys.add(it.next());
         }
      }

      return keys.iterator();
   }

   public Iterator getKeys(String key) {
      Set keys = new LinkedHashSet();
      Iterator i$ = this.configList.iterator();

      while(i$.hasNext()) {
         Configuration config = (Configuration)i$.next();
         Iterator it = config.getKeys(key);

         while(it.hasNext()) {
            keys.add(it.next());
         }
      }

      return keys.iterator();
   }

   public boolean isEmpty() {
      Iterator i$ = this.configList.iterator();

      Configuration config;
      do {
         if (!i$.hasNext()) {
            return true;
         }

         config = (Configuration)i$.next();
      } while(config.isEmpty());

      return false;
   }

   protected void clearPropertyDirect(String key) {
      Iterator i$ = this.configList.iterator();

      while(i$.hasNext()) {
         Configuration config = (Configuration)i$.next();
         config.clearProperty(key);
      }

   }

   public boolean containsKey(String key) {
      Iterator i$ = this.configList.iterator();

      Configuration config;
      do {
         if (!i$.hasNext()) {
            return false;
         }

         config = (Configuration)i$.next();
      } while(!config.containsKey(key));

      return true;
   }

   public List getList(String key, List defaultValue) {
      List list = new ArrayList();
      Iterator it = this.configList.iterator();

      while(it.hasNext() && list.isEmpty()) {
         Configuration config = (Configuration)it.next();
         if (config != this.inMemoryConfiguration && config.containsKey(key)) {
            appendListProperty(list, config, key);
         }
      }

      appendListProperty(list, this.inMemoryConfiguration, key);
      if (list.isEmpty()) {
         return defaultValue;
      } else {
         ListIterator lit = list.listIterator();

         while(lit.hasNext()) {
            lit.set(this.interpolate(lit.next()));
         }

         return list;
      }
   }

   public String[] getStringArray(String key) {
      List list = this.getList(key);
      String[] tokens = new String[list.size()];

      for(int i = 0; i < tokens.length; ++i) {
         tokens[i] = String.valueOf(list.get(i));
      }

      return tokens;
   }

   public Configuration getConfiguration(int index) {
      return (Configuration)this.configList.get(index);
   }

   public Configuration getInMemoryConfiguration() {
      return this.inMemoryConfiguration;
   }

   public Object clone() {
      try {
         CompositeConfiguration copy = (CompositeConfiguration)super.clone();
         copy.clearConfigurationListeners();
         copy.configList = new LinkedList();
         copy.inMemoryConfiguration = ConfigurationUtils.cloneConfiguration(this.getInMemoryConfiguration());
         copy.configList.add(copy.inMemoryConfiguration);
         Iterator i$ = this.configList.iterator();

         while(i$.hasNext()) {
            Configuration config = (Configuration)i$.next();
            if (config != this.getInMemoryConfiguration()) {
               copy.addConfiguration(ConfigurationUtils.cloneConfiguration(config));
            }
         }

         return copy;
      } catch (CloneNotSupportedException var4) {
         throw new ConfigurationRuntimeException(var4);
      }
   }

   public void setDelimiterParsingDisabled(boolean delimiterParsingDisabled) {
      if (this.inMemoryConfiguration instanceof AbstractConfiguration) {
         ((AbstractConfiguration)this.inMemoryConfiguration).setDelimiterParsingDisabled(delimiterParsingDisabled);
      }

      super.setDelimiterParsingDisabled(delimiterParsingDisabled);
   }

   public void setListDelimiter(char listDelimiter) {
      if (this.inMemoryConfiguration instanceof AbstractConfiguration) {
         ((AbstractConfiguration)this.inMemoryConfiguration).setListDelimiter(listDelimiter);
      }

      super.setListDelimiter(listDelimiter);
   }

   public Configuration getSource(String key) {
      if (key == null) {
         throw new IllegalArgumentException("Key must not be null!");
      } else {
         Configuration source = null;
         Iterator i$ = this.configList.iterator();

         while(i$.hasNext()) {
            Configuration conf = (Configuration)i$.next();
            if (conf.containsKey(key)) {
               if (source != null) {
                  throw new IllegalArgumentException("The key " + key + " is defined by multiple sources!");
               }

               source = conf;
            }
         }

         return source;
      }
   }

   private void replaceInMemoryConfiguration(Configuration config) {
      if (!this.inMemoryConfigIsChild) {
         this.configList.remove(this.inMemoryConfiguration);
      }

      this.inMemoryConfiguration = config;
   }

   private static void appendListProperty(List dest, Configuration config, String key) {
      Object value = config.getProperty(key);
      if (value != null) {
         if (value instanceof Collection) {
            Collection col = (Collection)value;
            dest.addAll(col);
         } else {
            dest.add(value);
         }
      }

   }
}
