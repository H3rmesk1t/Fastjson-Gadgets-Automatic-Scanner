package com.sun.syndication.feed.module;

import java.util.Date;

public interface SyModule extends Module {
   String URI = "http://purl.org/rss/1.0/modules/syndication/";
   String HOURLY = new String("hourly");
   String DAILY = new String("daily");
   String WEEKLY = new String("weekly");
   String MONTHLY = new String("monthly");
   String YEARLY = new String("yearly");

   String getUpdatePeriod();

   void setUpdatePeriod(String var1);

   int getUpdateFrequency();

   void setUpdateFrequency(int var1);

   Date getUpdateBase();

   void setUpdateBase(Date var1);
}
