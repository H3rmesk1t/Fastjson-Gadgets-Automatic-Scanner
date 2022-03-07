package org.apache.commons.configuration.reloading;

import org.apache.commons.configuration.FileConfiguration;

public class InvariantReloadingStrategy implements ReloadingStrategy {
   public void setConfiguration(FileConfiguration configuration) {
   }

   public void init() {
   }

   public boolean reloadingRequired() {
      return false;
   }

   public void reloadingPerformed() {
   }
}
