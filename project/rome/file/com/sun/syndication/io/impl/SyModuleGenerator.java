package com.sun.syndication.io.impl;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.SyModule;
import com.sun.syndication.io.ModuleGenerator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jdom.Element;
import org.jdom.Namespace;

public class SyModuleGenerator implements ModuleGenerator {
   private static final String SY_URI = "http://purl.org/rss/1.0/modules/syndication/";
   private static final Namespace SY_NS = Namespace.getNamespace("sy", "http://purl.org/rss/1.0/modules/syndication/");
   private static final Set NAMESPACES;

   public String getNamespaceUri() {
      return "http://purl.org/rss/1.0/modules/syndication/";
   }

   public Set getNamespaces() {
      return NAMESPACES;
   }

   public void generate(Module module, Element element) {
      SyModule syModule = (SyModule)module;
      Element updateFrequencyElement;
      if (syModule.getUpdatePeriod() != null) {
         updateFrequencyElement = new Element("updatePeriod", SY_NS);
         updateFrequencyElement.addContent(syModule.getUpdatePeriod());
         element.addContent(updateFrequencyElement);
      }

      updateFrequencyElement = new Element("updateFrequency", SY_NS);
      updateFrequencyElement.addContent(String.valueOf(syModule.getUpdateFrequency()));
      element.addContent(updateFrequencyElement);
      if (syModule.getUpdateBase() != null) {
         Element updateBaseElement = new Element("updateBase", SY_NS);
         updateBaseElement.addContent(DateParser.formatW3CDateTime(syModule.getUpdateBase()));
         element.addContent(updateBaseElement);
      }

   }

   static {
      Set nss = new HashSet();
      nss.add(SY_NS);
      NAMESPACES = Collections.unmodifiableSet(nss);
   }
}
