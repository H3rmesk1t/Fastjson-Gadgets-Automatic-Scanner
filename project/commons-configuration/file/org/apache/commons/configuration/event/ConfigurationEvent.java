package org.apache.commons.configuration.event;

import java.util.EventObject;

public class ConfigurationEvent extends EventObject {
   private static final long serialVersionUID = 3277238219073504136L;
   private int type;
   private String propertyName;
   private Object propertyValue;
   private boolean beforeUpdate;

   public ConfigurationEvent(Object source, int type, String propertyName, Object propertyValue, boolean beforeUpdate) {
      super(source);
      this.type = type;
      this.propertyName = propertyName;
      this.propertyValue = propertyValue;
      this.beforeUpdate = beforeUpdate;
   }

   public String getPropertyName() {
      return this.propertyName;
   }

   public Object getPropertyValue() {
      return this.propertyValue;
   }

   public int getType() {
      return this.type;
   }

   public boolean isBeforeUpdate() {
      return this.beforeUpdate;
   }
}
