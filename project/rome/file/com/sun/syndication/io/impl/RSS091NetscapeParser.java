package com.sun.syndication.io.impl;

import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

public class RSS091NetscapeParser extends RSS091UserlandParser {
   static final String ELEMENT_NAME = "rss";
   static final String PUBLIC_ID = "-//Netscape Communications//DTD RSS 0.91//EN";
   static final String SYSTEM_ID = "http://my.netscape.com/publish/formats/rss-0.91.dtd";

   public RSS091NetscapeParser() {
      this("rss_0.91N");
   }

   protected RSS091NetscapeParser(String type) {
      super(type);
   }

   public boolean isMyType(Document document) {
      boolean ok = false;
      Element rssRoot = document.getRootElement();
      ok = rssRoot.getName().equals("rss");
      if (ok) {
         ok = false;
         Attribute version = rssRoot.getAttribute("version");
         if (version != null) {
            ok = version.getValue().equals(this.getRSSVersion());
            if (ok) {
               ok = false;
               DocType docType = document.getDocType();
               if (docType != null) {
                  ok = "rss".equals(docType.getElementName());
                  ok = ok && "-//Netscape Communications//DTD RSS 0.91//EN".equals(docType.getPublicID());
                  ok = ok && "http://my.netscape.com/publish/formats/rss-0.91.dtd".equals(docType.getSystemID());
               }
            }
         }
      }

      return ok;
   }

   protected boolean isHourFormat24(Element rssRoot) {
      return false;
   }

   protected String getTextInputLabel() {
      return "textinput";
   }
}
