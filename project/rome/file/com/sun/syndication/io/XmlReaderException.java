package com.sun.syndication.io;

import java.io.IOException;
import java.io.InputStream;

public class XmlReaderException extends IOException {
   private String _bomEncoding;
   private String _xmlGuessEncoding;
   private String _xmlEncoding;
   private String _contentTypeMime;
   private String _contentTypeEncoding;
   private InputStream _is;

   public XmlReaderException(String msg, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
      this(msg, (String)null, (String)null, bomEnc, xmlGuessEnc, xmlEnc, is);
   }

   public XmlReaderException(String msg, String ctMime, String ctEnc, String bomEnc, String xmlGuessEnc, String xmlEnc, InputStream is) {
      super(msg);
      this._contentTypeMime = ctMime;
      this._contentTypeEncoding = ctEnc;
      this._bomEncoding = bomEnc;
      this._xmlGuessEncoding = xmlGuessEnc;
      this._xmlEncoding = xmlEnc;
      this._is = is;
   }

   public String getBomEncoding() {
      return this._bomEncoding;
   }

   public String getXmlGuessEncoding() {
      return this._xmlGuessEncoding;
   }

   public String getXmlEncoding() {
      return this._xmlEncoding;
   }

   public String getContentTypeMime() {
      return this._contentTypeMime;
   }

   public String getContentTypeEncoding() {
      return this._contentTypeEncoding;
   }

   public InputStream getInputStream() {
      return this._is;
   }
}
