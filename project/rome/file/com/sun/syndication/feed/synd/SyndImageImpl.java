package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.impl.CopyFromHelper;
import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyndImageImpl implements Serializable, SyndImage {
   private ObjectBean _objBean;
   private String _title;
   private String _url;
   private String _link;
   private String _description;
   private static final CopyFromHelper COPY_FROM_HELPER;

   public SyndImageImpl() {
      this._objBean = new ObjectBean(SyndImage.class, this);
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

   public String getTitle() {
      return this._title;
   }

   public void setTitle(String title) {
      this._title = title;
   }

   public String getUrl() {
      return this._url;
   }

   public void setUrl(String url) {
      this._url = url;
   }

   public String getLink() {
      return this._link;
   }

   public void setLink(String link) {
      this._link = link;
   }

   public String getDescription() {
      return this._description;
   }

   public void setDescription(String description) {
      this._description = description;
   }

   public Class getInterface() {
      return SyndImage.class;
   }

   public void copyFrom(Object syndImage) {
      COPY_FROM_HELPER.copy(this, syndImage);
   }

   static {
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("title", String.class);
      basePropInterfaceMap.put("url", String.class);
      basePropInterfaceMap.put("link", String.class);
      basePropInterfaceMap.put("description", String.class);
      Map basePropClassImplMap = Collections.EMPTY_MAP;
      COPY_FROM_HELPER = new CopyFromHelper(SyndImage.class, basePropInterfaceMap, basePropClassImplMap);
   }
}
