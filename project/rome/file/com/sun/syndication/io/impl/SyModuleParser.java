package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.SyModule;
import com.sun.syndication.feed.module.SyModuleImpl;
import com.sun.syndication.io.ModuleParser;
import org.jdom.Element;
import org.jdom.Namespace;

public class SyModuleParser implements ModuleParser {
   public String getNamespaceUri() {
      return "http://purl.org/rss/1.0/modules/syndication/";
   }

   private Namespace getDCNamespace() {
      return Namespace.getNamespace("http://purl.org/rss/1.0/modules/syndication/");
   }

   public Module parse(Element syndRoot) {
      boolean foundSomething = false;
      SyModule sm = new SyModuleImpl();
      Element e = syndRoot.getChild("updatePeriod", this.getDCNamespace());
      if (e != null) {
         foundSomething = true;
         sm.setUpdatePeriod(e.getText());
      }

      e = syndRoot.getChild("updateFrequency", this.getDCNamespace());
      if (e != null) {
         foundSomething = true;
         sm.setUpdateFrequency(Integer.parseInt(e.getText().trim()));
      }

      e = syndRoot.getChild("updateBase", this.getDCNamespace());
      if (e != null) {
         foundSomething = true;
         sm.setUpdateBase(DateParser.parseDate(e.getText()));
      }

      return foundSomething ? sm : null;
   }
}
