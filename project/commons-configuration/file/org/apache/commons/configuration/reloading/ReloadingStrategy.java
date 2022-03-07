package org.apache.commons.configuration.reloading;

import org.apache.commons.configuration.FileConfiguration;

public interface ReloadingStrategy {
   void setConfiguration(FileConfiguration var1);

   void init();

   boolean reloadingRequired();

   void reloadingPerformed();
}
