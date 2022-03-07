package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.impl.CopyFromHelper;
import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyndEnclosureImpl implements Serializable, SyndEnclosure {
   private ObjectBean _objBean;
   private String _url;
   private String _type;
   private long _length;
   private static final CopyFromHelper COPY_FROM_HELPER;

   public SyndEnclosureImpl() {
      this._objBean = new ObjectBean(SyndEnclosure.class, this);
   }

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

   public String getUrl() {
      return this._url;
   }

   public void setUrl(String url) {
      this._url = url;
   }

   public long getLength() {
      return this._length;
   }

   public void setLength(long length) {
      this._length = length;
   }

   public String getType() {
      return this._type;
   }

   public void setType(String type) {
      this._type = type;
   }

   public Class getInterface() {
      return SyndEnclosure.class;
   }

   public void copyFrom(Object obj) {
      COPY_FROM_HELPER.copy(this, obj);
   }

   static {
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("url", String.class);
      basePropInterfaceMap.put("type", String.class);
      basePropInterfaceMap.put("length", Long.TYPE);
      Map basePropClassImplMap = Collections.EMPTY_MAP;
      COPY_FROM_HELPER = new CopyFromHelper(SyndEnclosure.class, basePropInterfaceMap, basePropClassImplMap);
   }
}
