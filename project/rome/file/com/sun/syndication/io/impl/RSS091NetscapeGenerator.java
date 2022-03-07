package com.sun.syndication.io.impl;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

public class RSS091NetscapeGenerator extends RSS091UserlandGenerator {
   private String _version;

   public RSS091NetscapeGenerator() {
      this("rss_0.91N", "0.91");
   }

   protected RSS091NetscapeGenerator(String type, String version) {
      super(type, version);
   }

   protected Document createDocument(Element root) {
      Document doc = new Document(root);
      DocType docType = new DocType("rss", "-//Netscape Communications//DTD RSS 0.91//EN", "http://my.netscape.com/publish/formats/rss-0.91.dtd");
      doc.setDocType(docType);
      return doc;
   }

   protected String getTextInputLabel() {
      return "textinput";
   }

   protected boolean isHourFormat24() {
      return false;
   }
}
