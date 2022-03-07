package org.apache.commons.configuration;

import org.apache.commons.configuration.reloading.Reloadable;

public class HierarchicalReloadableConfiguration extends HierarchicalConfiguration implements Reloadable {
   private static final String LOCK_NAME = "HierarchicalReloadableConfigurationLock";
   private final Object reloadLock;

   public HierarchicalReloadableConfiguration() {
      this.reloadLock = new Lock("HierarchicalReloadableConfigurationLock");
   }

   public HierarchicalReloadableConfiguration(Object lock) {
      this.reloadLock = lock == null ? new Lock("HierarchicalReloadableConfigurationLock") : lock;
   }

   public HierarchicalReloadableConfiguration(HierarchicalConfiguration c) {
      super(c);
      this.reloadLock = new Lock("HierarchicalReloadableConfigurationLock");
   }

   public Object getReloadLock() {
      return this.reloadLock;
   }
}
