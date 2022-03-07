package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.impl.CopyFromHelper;
import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyndContentImpl implements Serializable, SyndContent {
   private ObjectBean _objBean;
   private String _type;
   private String _value;
   private String _mode;
   private static final CopyFromHelper COPY_FROM_HELPER;

   public SyndContentImpl() {
      this._objBean = new ObjectBean(SyndContent.class, this);
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
      this._mode = mode;
   }

   public String getValue() {
      return this._value;
   }

   public void setValue(String value) {
      this._value = value;
   }

   public Class getInterface() {
      return SyndContent.class;
   }

   public void copyFrom(Object obj) {
      COPY_FROM_HELPER.copy(this, obj);
   }

   static {
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("type", String.class);
      basePropInterfaceMap.put("value", String.class);
      Map basePropClassImplMap = Collections.EMPTY_MAP;
      COPY_FROM_HELPER = new CopyFromHelper(SyndContent.class, basePropInterfaceMap, basePropClassImplMap);
   }
}
