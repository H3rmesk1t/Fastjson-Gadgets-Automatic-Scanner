package org.apache.commons.configuration;

import org.apache.commons.lang.exception.NestableException;

public class ConfigurationException extends NestableException {
   private static final long serialVersionUID = -1316746661346991484L;

   public ConfigurationException() {
   }

   public ConfigurationException(String message) {
      super(message);
   }

   public ConfigurationException(Throwable cause) {
      super(cause);
   }

   public ConfigurationException(String message, Throwable cause) {
      super(message, cause);
   }
}
