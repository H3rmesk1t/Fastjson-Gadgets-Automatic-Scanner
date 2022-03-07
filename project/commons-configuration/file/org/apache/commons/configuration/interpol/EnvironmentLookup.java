package org.apache.commons.configuration.interpol;

import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.lang.text.StrLookup;

public class EnvironmentLookup extends StrLookup {
   private final EnvironmentConfiguration environmentConfig = new EnvironmentConfiguration();

   public String lookup(String key) {
      return this.environmentConfig.getString(key);
   }
}
