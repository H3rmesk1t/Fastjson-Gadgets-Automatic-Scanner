package org.apache.commons.configuration.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang.StringUtils;

public class DefaultConfigurationKey {
   private static final int INITIAL_SIZE = 32;
   private DefaultExpressionEngine expressionEngine;
   private StringBuilder keyBuffer;

   public DefaultConfigurationKey(DefaultExpressionEngine engine) {
      this.keyBuffer = new StringBuilder(32);
      this.setExpressionEngine(engine);
   }

   public DefaultConfigurationKey(DefaultExpressionEngine engine, String key) {
      this.setExpressionEngine(engine);
      this.keyBuffer = new StringBuilder(this.trim(key));
   }

   public DefaultExpressionEngine getExpressionEngine() {
      return this.expressionEngine;
   }

   public void setExpressionEngine(DefaultExpressionEngine expressionEngine) {
      if (expressionEngine == null) {
         throw new IllegalArgumentException("Expression engine must not be null!");
      } else {
         this.expressionEngine = expressionEngine;
      }
   }

   public DefaultConfigurationKey append(String property, boolean escape) {
      String key;
      if (escape && property != null) {
         key = this.escapeDelimiters(property);
      } else {
         key = property;
      }

      key = this.trim(key);
      if (this.keyBuffer.length() > 0 && !this.isAttributeKey(property) && key.length() > 0) {
         this.keyBuffer.append(this.getExpressionEngine().getPropertyDelimiter());
      }

      this.keyBuffer.append(key);
      return this;
   }

   public DefaultConfigurationKey append(String property) {
      return this.append(property, false);
   }

   public DefaultConfigurationKey appendIndex(int index) {
      this.keyBuffer.append(this.getExpressionEngine().getIndexStart());
      this.keyBuffer.append(index);
      this.keyBuffer.append(this.getExpressionEngine().getIndexEnd());
      return this;
   }

   public DefaultConfigurationKey appendAttribute(String attr) {
      this.keyBuffer.append(this.constructAttributeKey(attr));
      return this;
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

   public String toString() {
      return this.keyBuffer.toString();
   }

   public boolean isAttributeKey(String key) {
      if (key == null) {
         return false;
      } else {
         return key.startsWith(this.getExpressionEngine().getAttributeStart()) && (this.getExpressionEngine().getAttributeEnd() == null || key.endsWith(this.getExpressionEngine().getAttributeEnd()));
      }
   }

   public String constructAttributeKey(String key) {
      if (key == null) {
         return "";
      } else if (this.isAttributeKey(key)) {
         return key;
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append(this.getExpressionEngine().getAttributeStart()).append(key);
         if (this.getExpressionEngine().getAttributeEnd() != null) {
            buf.append(this.getExpressionEngine().getAttributeEnd());
         }

         return buf.toString();
      }
   }

   public String attributeName(String key) {
      return this.isAttributeKey(key) ? this.removeAttributeMarkers(key) : key;
   }

   public String trimLeft(String key) {
      if (key == null) {
         return "";
      } else {
         String result;
         for(result = key; this.hasLeadingDelimiter(result); result = result.substring(this.getExpressionEngine().getPropertyDelimiter().length())) {
         }

         return result;
      }
   }

   public String trimRight(String key) {
      if (key == null) {
         return "";
      } else {
         String result;
         for(result = key; this.hasTrailingDelimiter(result); result = result.substring(0, result.length() - this.getExpressionEngine().getPropertyDelimiter().length())) {
         }

         return result;
      }
   }

   public String trim(String key) {
      return this.trimRight(this.trimLeft(key));
   }

   public DefaultConfigurationKey.KeyIterator iterator() {
      return new DefaultConfigurationKey.KeyIterator();
   }

   private boolean hasTrailingDelimiter(String key) {
      return key.endsWith(this.getExpressionEngine().getPropertyDelimiter()) && (this.getExpressionEngine().getEscapedDelimiter() == null || !key.endsWith(this.getExpressionEngine().getEscapedDelimiter()));
   }

   private boolean hasLeadingDelimiter(String key) {
      return key.startsWith(this.getExpressionEngine().getPropertyDelimiter()) && (this.getExpressionEngine().getEscapedDelimiter() == null || !key.startsWith(this.getExpressionEngine().getEscapedDelimiter()));
   }

   private String removeAttributeMarkers(String key) {
      return key.substring(this.getExpressionEngine().getAttributeStart().length(), key.length() - (this.getExpressionEngine().getAttributeEnd() != null ? this.getExpressionEngine().getAttributeEnd().length() : 0));
   }

   private String unescapeDelimiters(String key) {
      return this.getExpressionEngine().getEscapedDelimiter() == null ? key : StringUtils.replace(key, this.getExpressionEngine().getEscapedDelimiter(), this.getExpressionEngine().getPropertyDelimiter());
   }

   private String escapeDelimiters(String key) {
      return this.getExpressionEngine().getEscapedDelimiter() != null && key.indexOf(this.getExpressionEngine().getPropertyDelimiter()) >= 0 ? StringUtils.replace(key, this.getExpressionEngine().getPropertyDelimiter(), this.getExpressionEngine().getEscapedDelimiter()) : key;
   }

   public class KeyIterator implements Iterator, Cloneable {
      private String current;
      private int startIndex;
      private int endIndex;
      private int indexValue;
      private boolean hasIndex;
      private boolean attribute;

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

      public boolean hasNext() {
         return this.endIndex < DefaultConfigurationKey.this.keyBuffer.length();
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
         return decorated && !this.isPropertyKey() ? DefaultConfigurationKey.this.constructAttributeKey(this.current) : this.current;
      }

      public boolean isAttribute() {
         return this.attribute || this.isAttributeEmulatingMode() && !this.hasNext();
      }

      public boolean isPropertyKey() {
         return !this.attribute;
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

      private String findNextIndices() {
         for(this.startIndex = this.endIndex; this.startIndex < DefaultConfigurationKey.this.length() && DefaultConfigurationKey.this.hasLeadingDelimiter(DefaultConfigurationKey.this.keyBuffer.substring(this.startIndex)); this.startIndex += DefaultConfigurationKey.this.getExpressionEngine().getPropertyDelimiter().length()) {
         }

         if (this.startIndex >= DefaultConfigurationKey.this.length()) {
            this.endIndex = DefaultConfigurationKey.this.length();
            this.startIndex = this.endIndex - 1;
            return DefaultConfigurationKey.this.keyBuffer.substring(this.startIndex, this.endIndex);
         } else {
            return this.nextKeyPart();
         }
      }

      private String nextKeyPart() {
         int attrIdx = DefaultConfigurationKey.this.keyBuffer.toString().indexOf(DefaultConfigurationKey.this.getExpressionEngine().getAttributeStart(), this.startIndex);
         if (attrIdx < 0 || attrIdx == this.startIndex) {
            attrIdx = DefaultConfigurationKey.this.length();
         }

         int delIdx = this.nextDelimiterPos(DefaultConfigurationKey.this.keyBuffer.toString(), this.startIndex, attrIdx);
         if (delIdx < 0) {
            delIdx = attrIdx;
         }

         this.endIndex = Math.min(attrIdx, delIdx);
         return DefaultConfigurationKey.this.unescapeDelimiters(DefaultConfigurationKey.this.keyBuffer.substring(this.startIndex, this.endIndex));
      }

      private int nextDelimiterPos(String key, int pos, int endPos) {
         int delimiterPos = pos;
         boolean found = false;

         do {
            delimiterPos = key.indexOf(DefaultConfigurationKey.this.getExpressionEngine().getPropertyDelimiter(), delimiterPos);
            if (delimiterPos < 0 || delimiterPos >= endPos) {
               return -1;
            }

            int escapePos = this.escapedPosition(key, delimiterPos);
            if (escapePos < 0) {
               found = true;
            } else {
               delimiterPos = escapePos;
            }
         } while(!found);

         return delimiterPos;
      }

      private int escapedPosition(String key, int pos) {
         if (DefaultConfigurationKey.this.getExpressionEngine().getEscapedDelimiter() == null) {
            return -1;
         } else {
            int escapeOffset = this.escapeOffset();
            if (escapeOffset >= 0 && escapeOffset <= pos) {
               int escapePos = key.indexOf(DefaultConfigurationKey.this.getExpressionEngine().getEscapedDelimiter(), pos - escapeOffset);
               return escapePos <= pos && escapePos >= 0 ? escapePos + DefaultConfigurationKey.this.getExpressionEngine().getEscapedDelimiter().length() : -1;
            } else {
               return -1;
            }
         }
      }

      private int escapeOffset() {
         return DefaultConfigurationKey.this.getExpressionEngine().getEscapedDelimiter().indexOf(DefaultConfigurationKey.this.getExpressionEngine().getPropertyDelimiter());
      }

      private boolean checkAttribute(String key) {
         if (DefaultConfigurationKey.this.isAttributeKey(key)) {
            this.current = DefaultConfigurationKey.this.removeAttributeMarkers(key);
            return true;
         } else {
            return false;
         }
      }

      private boolean checkIndex(String key) {
         boolean result = false;

         try {
            int idx = key.lastIndexOf(DefaultConfigurationKey.this.getExpressionEngine().getIndexStart());
            if (idx > 0) {
               int endidx = key.indexOf(DefaultConfigurationKey.this.getExpressionEngine().getIndexEnd(), idx);
               if (endidx > idx + 1) {
                  this.indexValue = Integer.parseInt(key.substring(idx + 1, endidx));
                  this.current = key.substring(0, idx);
                  result = true;
               }
            }
         } catch (NumberFormatException var5) {
            result = false;
         }

         return result;
      }

      private boolean isAttributeEmulatingMode() {
         return DefaultConfigurationKey.this.getExpressionEngine().getAttributeEnd() == null && StringUtils.equals(DefaultConfigurationKey.this.getExpressionEngine().getPropertyDelimiter(), DefaultConfigurationKey.this.getExpressionEngine().getAttributeStart());
      }
   }
}
