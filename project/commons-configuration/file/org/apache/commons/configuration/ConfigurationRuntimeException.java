package org.apache.commons.configuration;

import org.apache.commons.lang.exception.NestableRuntimeException;

public class ConfigurationRuntimeException extends NestableRuntimeException {
   private static final long serialVersionUID = -7838702245512140996L;

   public ConfigurationRuntimeException() {
   }

   public ConfigurationRuntimeException(String message) {
      super(message);
   }

   public ConfigurationRuntimeException(Throwable cause) {
      super(cause);
   }

   public ConfigurationRuntimeException(String message, Throwable cause) {
      super(message, cause);
   }
}
