package org.apache.commons.configuration.web;

import java.util.List;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertyConverter;

abstract class BaseWebConfiguration extends AbstractConfiguration {
   public boolean isEmpty() {
      return !this.getKeys().hasNext();
   }

   public boolean containsKey(String key) {
      return this.getProperty(key) != null;
   }

   public void clearProperty(String key) {
      throw new UnsupportedOperationException("Read only configuration");
   }

   protected void addPropertyDirect(String key, Object obj) {
      throw new UnsupportedOperationException("Read only configuration");
   }

   protected Object handleDelimiters(Object value) {
      if (!this.isDelimiterParsingDisabled() && value instanceof String) {
         List list = PropertyConverter.split((String)value, this.getListDelimiter());
         value = list.size() > 1 ? list : list.get(0);
      }

      return value;
   }
}
