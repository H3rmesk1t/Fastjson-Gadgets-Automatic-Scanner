package org.apache.commons.configuration.reloading;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManagedReloadingStrategy implements ReloadingStrategy, ManagedReloadingStrategyMBean {
   private Log log = LogFactory.getLog(ManagedReloadingStrategy.class);
   private FileConfiguration configuration;
   private boolean reloadingRequired;

   public void init() {
   }

   public void reloadingPerformed() {
      this.reloadingRequired = false;
   }

   public boolean reloadingRequired() {
      return this.reloadingRequired;
   }

   public void setConfiguration(FileConfiguration configuration) {
      this.configuration = configuration;
   }

   public void refresh() {
      this.log.info("Reloading configuration.");
      this.reloadingRequired = true;
      this.configuration.isEmpty();
   }
}
