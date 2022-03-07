package org.apache.commons.configuration;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** @deprecated */
@Deprecated
public class ConfigurationKey implements Serializable {
   public static final char PROPERTY_DELIMITER = '.';
   public static final String ESCAPED_DELIMITER = '.' + String.valueOf('.');
   private static final String ATTRIBUTE_START = "[@";
   private static final String ATTRIBUTE_END = "]";
   private static final char INDEX_START = '(';
   private static final char INDEX_END = ')';
   private static final int INITIAL_SIZE = 32;
   private static final long serialVersionUID = -4299732083605277656L;
   private StringBuilder keyBuffer;

   public ConfigurationKey() {
      this.keyBuffer = new StringBuilder(32);
   }

   public ConfigurationKey(String key) {
      this.keyBuffer = new StringBuilder(key);
      this.removeTrailingDelimiter();
   }

   public ConfigurationKey append(String property) {
      if (this.keyBuffer.length() > 0 && !this.hasDelimiter() && !isAttributeKey(property)) {
         this.keyBuffer.append('.');
      }

      this.keyBuffer.append(property);
      this.removeTrailingDelimiter();
      return this;
   }

   public ConfigurationKey appendIndex(int index) {
      this.keyBuffer.append('(').append(index);
      this.keyBuffer.append(')');
      return this;
   }

   public ConfigurationKey appendAttribute(String attr) {
      this.keyBuffer.append(constructAttributeKey(attr));
      return this;
   }

   public boolean isAttributeKey() {
      return isAttributeKey(this.keyBuffer.toString());
   }

   public static boolean isAttributeKey(String key) {
      return key != null && key.startsWith("[@") && key.endsWith("]");
   }

   public static String constructAttributeKey(String key) {
      StringBuilder buf = new StringBuilder();
      buf.append("[@").append(key).append("]");
      return buf.toString();
   }

   public static String attributeName(String key) {
      return isAttributeKey(key) ? removeAttributeMarkers(key) : key;
   }

   static String removeAttributeMarkers(String key) {
      return key.substring("[@".length(), key.length() - "]".length());
   }

   private boolean hasDelimiter() {
      int count = 0;

      for(int idx = this.keyBuffer.length() - 1; idx >= 0 && this.keyBuffer.charAt(idx) == '.'; --idx) {
         ++count;
      }

      return count % 2 != 0;
   }

   private void removeTrailingDelimiter() {
      while(this.hasDelimiter()) {
         this.keyBuffer.deleteCharAt(this.keyBuffer.length() - 1);
      }

   }

   public String toString() {
      return this.keyBuffer.toString();
   }

   public ConfigurationKey.KeyIterator iterator() {
      return new ConfigurationKey.KeyIterator();
   }

   public int length() {
      return this.keyBuffer.length();
   }

   public void setLength(int len) {
      this.keyBuffer.setLength(len);
   }

   public boolean equals(Object c) {
      return c == null ? false : this.keyBuffer.toString().equals(c.toString());
   }

   public int hashCode() {
      return String.valueOf(this.keyBuffer).hashCode();
   }

   public ConfigurationKey commonKey(ConfigurationKey other) {
      if (other == null) {
         throw new IllegalArgumentException("Other key must no be null!");
      } else {
         ConfigurationKey result = new ConfigurationKey();
         ConfigurationKey.KeyIterator it1 = this.iterator();
         ConfigurationKey.KeyIterator it2 = other.iterator();

         while(it1.hasNext() && it2.hasNext() && partsEqual(it1, it2)) {
            if (it1.isAttribute()) {
               result.appendAttribute(it1.currentKey());
            } else {
               result.append(it1.currentKey());
               if (it1.hasIndex) {
                  result.appendIndex(it1.getIndex());
               }
            }
         }

         return result;
      }
   }

   public ConfigurationKey differenceKey(ConfigurationKey other) {
      ConfigurationKey common = this.commonKey(other);
      ConfigurationKey result = new ConfigurationKey();
      if (common.length() < other.length()) {
         String k = other.toString().substring(common.length());

         int i;
         for(i = 0; i < k.length() && k.charAt(i) == '.'; ++i) {
         }

         if (i < k.length()) {
            result.append(k.substring(i));
         }
      }

      return result;
   }

   private static boolean partsEqual(ConfigurationKey.KeyIterator it1, ConfigurationKey.KeyIterator it2) {
      return it1.nextKey().equals(it2.nextKey()) && it1.getIndex() == it2.getIndex() && it1.isAttribute() == it2.isAttribute();
   }

   public class KeyIterator implements Iterator, Cloneable {
      private String current;
      private int startIndex;
      private int endIndex;
      private int indexValue;
      private boolean hasIndex;
      private boolean attribute;

      private String findNextIndices() {
         for(this.startIndex = this.endIndex; this.startIndex < ConfigurationKey.this.keyBuffer.length() && ConfigurationKey.this.keyBuffer.charAt(this.startIndex) == '.'; ++this.startIndex) {
         }

         if (this.startIndex >= ConfigurationKey.this.keyBuffer.length()) {
            this.endIndex = ConfigurationKey.this.keyBuffer.length();
            this.startIndex = this.endIndex - 1;
            return ConfigurationKey.this.keyBuffer.substring(this.startIndex, this.endIndex);
         } else {
            return this.nextKeyPart();
         }
      }

      private String nextKeyPart() {
         StringBuilder key = new StringBuilder(32);
         int idx = this.startIndex;
         int endIdx = ConfigurationKey.this.keyBuffer.toString().indexOf("[@", this.startIndex);
         if (endIdx < 0 || endIdx == this.startIndex) {
            endIdx = ConfigurationKey.this.keyBuffer.length();
         }

         boolean found = false;

         while(!found && idx < endIdx) {
            char c = ConfigurationKey.this.keyBuffer.charAt(idx);
            if (c == '.') {
               if (idx != endIdx - 1 && ConfigurationKey.this.keyBuffer.charAt(idx + 1) == '.') {
                  ++idx;
               } else {
                  found = true;
               }
            }

            if (!found) {
               key.append(c);
               ++idx;
            }
         }

         this.endIndex = idx;
         return key.toString();
      }

      public String nextKey() {
         return this.nextKey(false);
      }

      public String nextKey(boolean decorated) {
         if (!this.hasNext()) {
            throw new NoSuchElementException("No more key parts!");
         } else {
            this.hasIndex = false;
            this.indexValue = -1;
            String key = this.findNextIndices();
            this.current = key;
            this.hasIndex = this.checkIndex(key);
            this.attribute = this.checkAttribute(this.current);
            return this.currentKey(decorated);
         }
      }

      private boolean checkAttribute(String key) {
         if (ConfigurationKey.isAttributeKey(key)) {
            this.current = ConfigurationKey.removeAttributeMarkers(key);
            return true;
         } else {
            return false;
         }
      }

      private boolean checkIndex(String key) {
         boolean result = false;
         int idx = key.lastIndexOf(40);
         if (idx > 0) {
            int endidx = key.indexOf(41, idx);
            if (endidx > idx + 1) {
               this.indexValue = Integer.parseInt(key.substring(idx + 1, endidx));
               this.current = key.substring(0, idx);
               result = true;
            }
         }

         return result;
      }

      public boolean hasNext() {
         return this.endIndex < ConfigurationKey.this.keyBuffer.length();
      }

      public Object next() {
         return this.nextKey();
      }

      public void remove() {
         throw new UnsupportedOperationException("Remove not supported!");
      }

      public String currentKey() {
         return this.currentKey(false);
      }

      public String currentKey(boolean decorated) {
         return decorated && this.isAttribute() ? ConfigurationKey.constructAttributeKey(this.current) : this.current;
      }

      public boolean isAttribute() {
         return this.attribute;
      }

      public int getIndex() {
         return this.indexValue;
      }

      public boolean hasIndex() {
         return this.hasIndex;
      }

      public Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            return null;
         }
      }
   }
}
