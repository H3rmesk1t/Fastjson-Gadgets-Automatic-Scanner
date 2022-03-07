package com.sun.syndication.feed.synd;

import com.sun.syndication.feed.module.DCSubject;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

class SyndCategoryListFacade extends AbstractList {
   private List _subjects;

   public SyndCategoryListFacade() {
      this(new ArrayList());
   }

   public SyndCategoryListFacade(List subjects) {
      this._subjects = subjects;
   }

   public Object get(int index) {
      return new SyndCategoryImpl((DCSubject)this._subjects.get(index));
   }

   public int size() {
      return this._subjects.size();
   }

   public Object set(int index, Object obj) {
      SyndCategoryImpl sCat = (SyndCategoryImpl)obj;
      DCSubject subject = sCat != null ? sCat.getSubject() : null;
      subject = (DCSubject)this._subjects.set(index, subject);
      return subject != null ? new SyndCategoryImpl(subject) : null;
   }

   public void add(int index, Object obj) {
      SyndCategoryImpl sCat = (SyndCategoryImpl)obj;
      DCSubject subject = sCat != null ? sCat.getSubject() : null;
      this._subjects.add(index, subject);
   }

   public Object remove(int index) {
      DCSubject subject = (DCSubject)this._subjects.remove(index);
      return subject != null ? new SyndCategoryImpl(subject) : null;
   }

   public static List convertElementsSyndCategoryToSubject(List cList) {
      List sList = null;
      if (cList != null) {
         sList = new ArrayList();

         for(int i = 0; i < cList.size(); ++i) {
            SyndCategoryImpl sCat = (SyndCategoryImpl)cList.get(i);
            DCSubject subject = null;
            if (sCat != null) {
               subject = sCat.getSubject();
            }

            sList.add(subject);
         }
      }

      return sList;
   }
}
