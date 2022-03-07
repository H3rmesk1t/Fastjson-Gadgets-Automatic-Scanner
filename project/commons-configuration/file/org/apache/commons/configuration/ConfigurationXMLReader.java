package org.apache.commons.configuration;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

public abstract class ConfigurationXMLReader implements XMLReader {
   protected static final String NS_URI = "";
   private static final String DEFAULT_ROOT_NAME = "config";
   private static final Attributes EMPTY_ATTRS = new AttributesImpl();
   private ContentHandler contentHandler;
   private SAXException exception;
   private String rootName;

   protected ConfigurationXMLReader() {
      this.setRootName("config");
   }

   public void parse(String systemId) throws IOException, SAXException {
      this.parseConfiguration();
   }

   public void parse(InputSource input) throws IOException, SAXException {
      this.parseConfiguration();
   }

   public boolean getFeature(String name) {
      return false;
   }

   public void setFeature(String name, boolean value) {
   }

   public ContentHandler getContentHandler() {
      return this.contentHandler;
   }

   public void setContentHandler(ContentHandler handler) {
      this.contentHandler = handler;
   }

   public DTDHandler getDTDHandler() {
      return null;
   }

   public void setDTDHandler(DTDHandler handler) {
   }

   public EntityResolver getEntityResolver() {
      return null;
   }

   public void setEntityResolver(EntityResolver resolver) {
   }

   public ErrorHandler getErrorHandler() {
      return null;
   }

   public void setErrorHandler(ErrorHandler handler) {
   }

   public Object getProperty(String name) {
      return null;
   }

   public void setProperty(String name, Object value) {
   }

   public String getRootName() {
      return this.rootName;
   }

   public void setRootName(String string) {
      this.rootName = string;
   }

   protected void fireElementStart(String name, Attributes attribs) {
      if (this.getException() == null) {
         try {
            Attributes at = attribs == null ? EMPTY_ATTRS : attribs;
            this.getContentHandler().startElement("", name, name, at);
         } catch (SAXException var4) {
            this.exception = var4;
         }
      }

   }

   protected void fireElementEnd(String name) {
      if (this.getException() == null) {
         try {
            this.getContentHandler().endElement("", name, name);
         } catch (SAXException var3) {
            this.exception = var3;
         }
      }

   }

   protected void fireCharacters(String text) {
      if (this.getException() == null) {
         try {
            char[] ch = text.toCharArray();
            this.getContentHandler().characters(ch, 0, ch.length);
         } catch (SAXException var3) {
            this.exception = var3;
         }
      }

   }

   public SAXException getException() {
      return this.exception;
   }

   protected void parseConfiguration() throws IOException, SAXException {
      if (this.getParsedConfiguration() == null) {
         throw new IOException("No configuration specified!");
      } else {
         if (this.getContentHandler() != null) {
            this.exception = null;
            this.getContentHandler().startDocument();
            this.processKeys();
            if (this.getException() != null) {
               throw this.getException();
            }

            this.getContentHandler().endDocument();
         }

      }
   }

   public abstract Configuration getParsedConfiguration();

   protected abstract void processKeys() throws IOException, SAXException;
}
