package com.sun.syndication.feed.module.impl;

import com.sun.syndication.feed.module.Module;
import java.util.ArrayList;
import java.util.List;

public class ModuleUtils {
   public static List cloneModules(List modules) {
      List cModules = null;
      if (modules != null) {
         cModules = new ArrayList();

         for(int i = 0; i < modules.size(); ++i) {
            Module module = (Module)modules.get(i);

            try {
               Object c = module.clone();
               cModules.add(c);
            } catch (Exception var5) {
               throw new RuntimeException("Cloning modules", var5);
            }
         }
      }

      return cModules;
   }

   public static Module getModule(List modules, String uri) {
      Module module = null;

      for(int i = 0; module == null && modules != null && i < modules.size(); ++i) {
         module = (Module)modules.get(i);
         if (!module.getUri().equals(uri)) {
            module = null;
         }
      }

      return module;
   }
}
