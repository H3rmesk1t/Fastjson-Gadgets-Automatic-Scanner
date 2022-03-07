package com.sun.syndication.feed.module;

import com.sun.syndication.feed.impl.CopyFromHelper;
import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DCSubjectImpl implements Cloneable, Serializable, DCSubject {
   private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
   private String _taxonomyUri;
   private String _value;
   private static final CopyFromHelper COPY_FROM_HELPER;

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

   public String getTaxonomyUri() {
      return this._taxonomyUri;
   }

   public void setTaxonomyUri(String taxonomyUri) {
      this._taxonomyUri = taxonomyUri;
   }

   public String getValue() {
      return this._value;
   }

   public void setValue(String value) {
      this._value = value;
   }

   public Class getInterface() {
      return DCSubject.class;
   }

   public void copyFrom(Object obj) {
      COPY_FROM_HELPER.copy(this, obj);
   }

   static {
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("taxonomyUri", String.class);
      basePropInterfaceMap.put("value", String.class);
      Map basePropClassImplMap = Collections.EMPTY_MAP;
      COPY_FROM_HELPER = new CopyFromHelper(DCSubject.class, basePropInterfaceMap, basePropClassImplMap);
   }
}
