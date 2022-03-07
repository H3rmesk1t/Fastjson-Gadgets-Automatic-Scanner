package org.apache.commons.configuration;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentConfiguration extends MapConfiguration {
   public EnvironmentConfiguration() {
      super((Map)(new HashMap(System.getenv())));
   }

   protected void addPropertyDirect(String key, Object value) {
      throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
   }

   public void clearProperty(String key) {
      throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
   }

   public void clear() {
      throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
   }
}
