package org.apache.commons.configuration.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

public class ServletConfiguration extends BaseWebConfiguration {
   protected ServletConfig config;

   public ServletConfiguration(Servlet servlet) {
      this(servlet.getServletConfig());
   }

   public ServletConfiguration(ServletConfig config) {
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
