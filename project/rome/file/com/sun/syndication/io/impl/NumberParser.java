package com.sun.syndication.io.impl;

public class NumberParser {
   private NumberParser() {
   }

   public static Long parseLong(String str) {
      if (null != str) {
         try {
            return Long.parseLong(str.trim());
         } catch (Exception var2) {
         }
      }

      return null;
   }

   public static Integer parseInt(String str) {
      if (null != str) {
         try {
            return Integer.parseInt(str.trim());
         } catch (Exception var2) {
         }
      }

      return null;
   }

   public static Float parseFloat(String str) {
      if (null != str) {
         try {
            return Float.parseFloat(str.trim());
         } catch (Exception var2) {
         }
      }

      return null;
   }

   public static float parseFloat(String str, float def) {
      Float result = parseFloat(str);
      return result == null ? def : result;
   }

   public static long parseLong(String str, long def) {
      Long ret = parseLong(str);
      return null == ret ? def : ret;
   }
}
