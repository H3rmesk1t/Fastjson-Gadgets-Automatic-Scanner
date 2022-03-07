package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class SyndLinkImpl implements Cloneable, Serializable, SyndLink {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _href;
   private String _rel;
   private String _type;
   private String _hreflang;
   private String _title;
   private long _length;

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

   public String getRel() {
      return this._rel;
   }

   public void setRel(String rel) {
      this._rel = rel;
   }

   public String getType() {
      return this._type;
   }

   public void setType(String type) {
      this._type = type;
   }

   public String getHref() {
      return this._href;
   }

   public void setHref(String href) {
      this._href = href;
   }

   public String getTitle() {
      return this._title;
   }

   public void setTitle(String title) {
      this._title = title;
   }

   public String getHreflang() {
      return this._hreflang;
   }

   public void setHreflang(String hreflang) {
      this._hreflang = hreflang;
   }

   public long getLength() {
      return this._length;
   }

   public void setLength(long length) {
      this._length = length;
   }
}
