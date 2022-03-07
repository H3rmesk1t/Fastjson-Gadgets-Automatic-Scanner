package org.apache.commons.configuration;

import java.util.Iterator;
import java.util.NoSuchElementException;

class PrefixedKeysIterator implements Iterator {
   private final Iterator iterator;
   private final String prefix;
   private String nextElement;
   private boolean nextElementSet;

   public PrefixedKeysIterator(Iterator wrappedIterator, String keyPrefix) {
      this.iterator = wrappedIterator;
      this.prefix = keyPrefix;
   }

   public boolean hasNext() {
      return this.nextElementSet || this.setNextElement();
   }

   public String next() {
      if (!this.nextElementSet && !this.setNextElement()) {
         throw new NoSuchElementException();
      } else {
         this.nextElementSet = false;
         return this.nextElement;
      }
   }

   public void remove() {
      if (this.nextElementSet) {
         throw new IllegalStateException("remove() cannot be called");
      } else {
         this.iterator.remove();
      }
   }

   private boolean setNextElement() {
      while(true) {
         if (this.iterator.hasNext()) {
            String key = (String)this.iterator.next();
            if (!key.startsWith(this.prefix + ".") && !key.equals(this.prefix)) {
               continue;
            }

            this.nextElement = key;
            this.nextElementSet = true;
            return true;
         }

         return false;
      }
   }
}
