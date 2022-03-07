package org.apache.commons.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public interface Configuration {
   Configuration subset(String var1);

   boolean isEmpty();

   boolean containsKey(String var1);

   void addProperty(String var1, Object var2);

   void setProperty(String var1, Object var2);

   void clearProperty(String var1);

   void clear();

   Object getProperty(String var1);

   Iterator getKeys(String var1);

   Iterator getKeys();

   Properties getProperties(String var1);

   boolean getBoolean(String var1);

   boolean getBoolean(String var1, boolean var2);

   Boolean getBoolean(String var1, Boolean var2);

   byte getByte(String var1);

   byte getByte(String var1, byte var2);

   Byte getByte(String var1, Byte var2);

   double getDouble(String var1);

   double getDouble(String var1, double var2);

   Double getDouble(String var1, Double var2);

   float getFloat(String var1);

   float getFloat(String var1, float var2);

   Float getFloat(String var1, Float var2);

   int getInt(String var1);

   int getInt(String var1, int var2);

   Integer getInteger(String var1, Integer var2);

   long getLong(String var1);

   long getLong(String var1, long var2);

   Long getLong(String var1, Long var2);

   short getShort(String var1);

   short getShort(String var1, short var2);

   Short getShort(String var1, Short var2);

   BigDecimal getBigDecimal(String var1);

   BigDecimal getBigDecimal(String var1, BigDecimal var2);

   BigInteger getBigInteger(String var1);

   BigInteger getBigInteger(String var1, BigInteger var2);

   String getString(String var1);

   String getString(String var1, String var2);

   String[] getStringArray(String var1);

   List getList(String var1);

   List getList(String var1, List var2);
}
