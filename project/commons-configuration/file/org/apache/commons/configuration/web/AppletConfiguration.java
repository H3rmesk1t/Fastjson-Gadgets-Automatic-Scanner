package org.apache.commons.configuration.web;

import java.applet.Applet;
import java.util.Arrays;
import java.util.Iterator;

public class AppletConfiguration extends BaseWebConfiguration {
   protected Applet applet;

   public AppletConfiguration(Applet applet) {
      this.applet = applet;
   }

   public Object getProperty(String key) {
      return this.handleDelimiters(this.applet.getParameter(key));
   }

   public Iterator getKeys() {
      String[][] paramsInfo = this.applet.getParameterInfo();
      String[] keys = new String[paramsInfo != null ? paramsInfo.length : 0];

      for(int i = 0; i < keys.length; ++i) {
         keys[i] = paramsInfo[i][0];
      }

      return Arrays.asList(keys).iterator();
   }
}
