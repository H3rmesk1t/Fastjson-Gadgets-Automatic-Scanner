package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Content implements Cloneable, Serializable {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _type;
   private String _value;
   private String _src;
   public static final String TEXT = "text";
   public static final String HTML = "html";
   public static final String XHTML = "xhtml";
   public static final String XML = "xml";
   public static final String BASE64 = "base64";
   public static final String ESCAPED = "escaped";
   private String _mode;
   private static final Set MODES = new HashSet();

   public Object clone() throws CloneNotSupportedException {
      return this._objBean.clone();
   }

   public boolean equals(Object other) {
      return this._objBean.equals(other);
   }

   public int hashCode() {
      return this._objBean.hashCode();
   }

   public String toString() {
      return this._objBean.toString();
   }

   public String getType() {
      return this._type;
   }

   public void setType(String type) {
      this._type = type;
   }

   public String getMode() {
      return this._mode;
   }

   public void setMode(String mode) {
      mode = mode != null ? mode.toLowerCase() : null;
      if (mode != null && MODES.contains(mode)) {
         this._mode = mode;
      } else {
         throw new IllegalArgumentException("Invalid mode [" + mode + "]");
      }
   }

   public String getValue() {
      return this._value;
   }

   public void setValue(String value) {
      this._value = value;
   }

   public String getSrc() {
      return this._src;
   }

   public void setSrc(String src) {
      this._src = src;
   }

   static {
      MODES.add("xml");
      MODES.add("base64");
      MODES.add("escaped");
   }
}
