package com.sun.syndication.feed.module;

import com.sun.syndication.feed.impl.CopyFromHelper;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SyModuleImpl extends ModuleImpl implements SyModule {
   private static final Set PERIODS = new HashSet();
   private String _updatePeriod;
   private int _updateFrequency;
   private Date _updateBase;
   private static final CopyFromHelper COPY_FROM_HELPER;

   public SyModuleImpl() {
      super(SyModule.class, "http://purl.org/rss/1.0/modules/syndication/");
   }

   public String getUpdatePeriod() {
      return this._updatePeriod;
   }

   public void setUpdatePeriod(String updatePeriod) {
      if (!PERIODS.contains(updatePeriod)) {
         throw new IllegalArgumentException("Invalid period [" + updatePeriod + "]");
      } else {
         this._updatePeriod = updatePeriod;
      }
   }

   public int getUpdateFrequency() {
      return this._updateFrequency;
   }

   public void setUpdateFrequency(int updateFrequency) {
      this._updateFrequency = updateFrequency;
   }

   public Date getUpdateBase() {
      return this._updateBase;
   }

   public void setUpdateBase(Date updateBase) {
      this._updateBase = updateBase;
   }

   public Class getInterface() {
      return SyModule.class;
   }

   public void copyFrom(Object obj) {
      COPY_FROM_HELPER.copy(this, obj);
   }

   static {
      PERIODS.add(HOURLY);
      PERIODS.add(DAILY);
      PERIODS.add(WEEKLY);
      PERIODS.add(MONTHLY);
      PERIODS.add(YEARLY);
      Map basePropInterfaceMap = new HashMap();
      basePropInterfaceMap.put("updatePeriod", String.class);
      basePropInterfaceMap.put("updateFrequency", Integer.TYPE);
      basePropInterfaceMap.put("updateBase", Date.class);
      Map basePropClassImplMap = Collections.EMPTY_MAP;
      COPY_FROM_HELPER = new CopyFromHelper(SyModule.class, basePropInterfaceMap, basePropClassImplMap);
   }
}
