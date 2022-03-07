package org.apache.commons.configuration;

import java.util.Iterator;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;

public class SubsetConfiguration extends AbstractConfiguration {
   protected Configuration parent;
   protected String prefix;
   protected String delimiter;

   public SubsetConfiguration(Configuration parent, String prefix) {
      this.parent = parent;
      this.prefix = prefix;
   }

   public SubsetConfiguration(Configuration parent, String prefix, String delimiter) {
      this.parent = parent;
      this.prefix = prefix;
      this.delimiter = delimiter;
   }

   protected String getParentKey(String key) {
      if (!"".equals(key) && key != null) {
         return this.delimiter == null ? this.prefix + key : this.prefix + this.delimiter + key;
      } else {
         return this.prefix;
      }
   }

   protected String getChildKey(String key) {
      if (!key.startsWith(this.prefix)) {
         throw new IllegalArgumentException("The parent key '" + key + "' is not in the subset.");
      } else {
         String modifiedKey = null;
         if (key.length() == this.prefix.length()) {
            modifiedKey = "";
         } else {
            int i = this.prefix.length() + (this.delimiter != null ? this.delimiter.length() : 0);
            modifiedKey = key.substring(i);
         }

         return modifiedKey;
      }
   }

   public Configuration getParent() {
      return this.parent;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public Configuration subset(String prefix) {
      return this.parent.subset(this.getParentKey(prefix));
   }

   public boolean isEmpty() {
      return !this.getKeys().hasNext();
   }

   public boolean containsKey(String key) {
      return this.parent.containsKey(this.getParentKey(key));
   }

   public void addPropertyDirect(String key, Object value) {
      this.parent.addProperty(this.getParentKey(key), value);
   }

   protected void clearPropertyDirect(String key) {
      this.parent.clearProperty(this.getParentKey(key));
   }

   public Object getProperty(String key) {
      return this.parent.getProperty(this.getParentKey(key));
   }

   public Iterator getKeys(String prefix) {
      return new SubsetConfiguration.SubsetIterator(this.parent.getKeys(this.getParentKey(prefix)));
   }

   public Iterator getKeys() {
      return new SubsetConfiguration.SubsetIterator(this.parent.getKeys(this.prefix));
   }

   protected Object interpolate(Object base) {
      if (this.delimiter == null && "".equals(this.prefix)) {
         return super.interpolate(base);
      } else {
         SubsetConfiguration config = new SubsetConfiguration(this.parent, "");
         ConfigurationInterpolator interpolator = config.getInterpolator();
         this.getInterpolator().registerLocalLookups(interpolator);
         if (this.parent instanceof AbstractConfiguration) {
            interpolator.setParentInterpolator(((AbstractConfiguration)this.parent).getInterpolator());
         }

         return config.interpolate(base);
      }
   }

   protected String interpolate(String base) {
      return super.interpolate(base);
   }

   public void setThrowExceptionOnMissing(boolean throwExceptionOnMissing) {
      if (this.parent instanceof AbstractConfiguration) {
         ((AbstractConfiguration)this.parent).setThrowExceptionOnMissing(throwExceptionOnMissing);
      } else {
         super.setThrowExceptionOnMissing(throwExceptionOnMissing);
      }

   }

   public boolean isThrowExceptionOnMissing() {
      return this.parent instanceof AbstractConfiguration ? ((AbstractConfiguration)this.parent).isThrowExceptionOnMissing() : super.isThrowExceptionOnMissing();
   }

   public char getListDelimiter() {
      return this.parent instanceof AbstractConfiguration ? ((AbstractConfiguration)this.parent).getListDelimiter() : super.getListDelimiter();
   }

   public void setListDelimiter(char delim) {
      if (this.parent instanceof AbstractConfiguration) {
         ((AbstractConfiguration)this.parent).setListDelimiter(delim);
      } else {
         super.setListDelimiter(delim);
      }

   }

   public boolean isDelimiterParsingDisabled() {
      return this.parent instanceof AbstractConfiguration ? ((AbstractConfiguration)this.parent).isDelimiterParsingDisabled() : super.isDelimiterParsingDisabled();
   }

   public void setDelimiterParsingDisabled(boolean delimiterParsingDisabled) {
      if (this.parent instanceof AbstractConfiguration) {
         ((AbstractConfiguration)this.parent).setDelimiterParsingDisabled(delimiterParsingDisabled);
      } else {
         super.setDelimiterParsingDisabled(delimiterParsingDisabled);
      }

   }

   private class SubsetIterator implements Iterator {
      private final Iterator parentIterator;

      public SubsetIterator(Iterator it) {
         this.parentIterator = it;
      }

      public boolean hasNext() {
         return this.parentIterator.hasNext();
      }

      public String next() {
         return SubsetConfiguration.this.getChildKey((String)this.parentIterator.next());
      }

      public void remove() {
         this.parentIterator.remove();
      }
   }
}
