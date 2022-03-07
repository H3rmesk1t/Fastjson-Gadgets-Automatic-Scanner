package org.apache.commons.configuration.event;

public class ConfigurationErrorEvent extends ConfigurationEvent {
   private static final long serialVersionUID = -7433184493062648409L;
   private Throwable cause;

   public ConfigurationErrorEvent(Object source, int type, String propertyName, Object propertyValue, Throwable cause) {
      super(source, type, propertyName, propertyValue, true);
      this.cause = cause;
   }

   public Throwable getCause() {
      return this.cause;
   }
}
