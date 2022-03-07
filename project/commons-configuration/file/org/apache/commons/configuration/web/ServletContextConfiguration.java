package org.apache.commons.configuration.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

public class ServletContextConfiguration extends BaseWebConfiguration {
   protected ServletContext context;

   public ServletContextConfiguration(Servlet servlet) {
      this.context = servlet.getServletConfig().getServletContext();
   }

   public ServletContextConfiguration(ServletContext context) {
      this.context = context;
   }

   public Object getProperty(String key) {
      return this.handleDelimiters(this.context.getInitParameter(key));
   }

   public Iterator getKeys() {
      Enumeration en = this.context.getInitParameterNames();
      return Collections.list(en).iterator();
   }
}
