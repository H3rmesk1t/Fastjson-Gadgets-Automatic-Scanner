package org.apache.commons.configuration.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.FilterConfig;

public class ServletFilterConfiguration extends BaseWebConfiguration {
   protected FilterConfig config;

   public ServletFilterConfiguration(FilterConfig config) {
      this.config = config;
   }

   public Object getProperty(String key) {
      return this.handleDelimiters(this.config.getInitParameter(key));
   }

   public Iterator getKeys() {
      Enumeration en = this.config.getInitParameterNames();
      return Collections.list(en).iterator();
   }
}
