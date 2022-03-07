package com.sun.syndication.io;

import org.jdom.JDOMException;
import org.xml.sax.XMLReader;

public class SAXBuilder extends org.jdom.input.SAXBuilder {
   public SAXBuilder(boolean _validate) {
      super(_validate);
   }

   public XMLReader createParser() throws JDOMException {
      return super.createParser();
   }
}
