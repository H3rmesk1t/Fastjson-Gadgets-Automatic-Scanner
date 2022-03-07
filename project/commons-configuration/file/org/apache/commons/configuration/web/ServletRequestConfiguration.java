package org.apache.commons.configuration.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;

public class ServletRequestConfiguration extends BaseWebConfiguration {
   protected ServletRequest request;

   public ServletRequestConfiguration(ServletRequest request) {
      this.request = request;
   }

   public Object getProperty(String key) {
      String[] values = this.request.getParameterValues(key);
      if (values != null && values.length != 0) {
         if (values.length == 1) {
            return this.handleDelimiters(values[0]);
         } else {
            List result = new ArrayList(values.length);

            for(int i = 0; i < values.length; ++i) {
               Object val = this.handleDelimiters(values[i]);
               if (val instanceof Collection) {
                  result.addAll((Collection)val);
               } else {
                  result.add(val);
               }
            }

            return result;
         }
      } else {
         return null;
      }
   }

   public Iterator getKeys() {
      Map parameterMap = this.request.getParameterMap();
      return parameterMap.keySet().iterator();
   }
}
