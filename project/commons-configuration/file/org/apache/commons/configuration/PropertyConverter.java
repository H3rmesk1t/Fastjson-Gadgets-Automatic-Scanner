package org.apache.commons.configuration;

import java.awt.Color;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

public final class PropertyConverter {
   static final char LIST_ESC_CHAR = '\\';
   static final String LIST_ESCAPE = String.valueOf('\\');
   private static final String HEX_PREFIX = "0x";
   private static final int HEX_RADIX = 16;
   private static final String BIN_PREFIX = "0b";
   private static final int BIN_RADIX = 2;
   private static final Class[] CONSTR_ARGS = new Class[]{String.class};
   private static final String INTERNET_ADDRESS_CLASSNAME = "javax.mail.internet.InternetAddress";

   private PropertyConverter() {
   }

   static Object to(Class cls, Object value, Object[] params) throws ConversionException {
      if (cls.isInstance(value)) {
         return value;
      } else if (!Boolean.class.equals(cls) && !Boolean.TYPE.equals(cls)) {
         if (!Character.class.equals(cls) && !Character.TYPE.equals(cls)) {
            if (!Number.class.isAssignableFrom(cls) && !cls.isPrimitive()) {
               if (Date.class.equals(cls)) {
                  return toDate(value, (String)params[0]);
               } else if (Calendar.class.equals(cls)) {
                  return toCalendar(value, (String)params[0]);
               } else if (URL.class.equals(cls)) {
                  return toURL(value);
               } else if (Locale.class.equals(cls)) {
                  return toLocale(value);
               } else if (isEnum(cls)) {
                  return convertToEnum(cls, value);
               } else if (Color.class.equals(cls)) {
                  return toColor(value);
               } else if (cls.getName().equals("javax.mail.internet.InternetAddress")) {
                  return toInternetAddress(value);
               } else if (InetAddress.class.isAssignableFrom(cls)) {
                  return toInetAddress(value);
               } else {
                  throw new ConversionException("The value '" + value + "' (" + value.getClass() + ")" + " can't be converted to a " + cls.getName() + " object");
               }
            } else if (!Integer.class.equals(cls) && !Integer.TYPE.equals(cls)) {
               if (Long.class.equals(cls) || Long.TYPE.equals(cls)) {
                  return toLong(value);
               } else if (!Byte.class.equals(cls) && !Byte.TYPE.equals(cls)) {
                  if (!Short.class.equals(cls) && !Short.TYPE.equals(cls)) {
                     if (!Float.class.equals(cls) && !Float.TYPE.equals(cls)) {
                        if (!Double.class.equals(cls) && !Double.TYPE.equals(cls)) {
                           if (BigInteger.class.equals(cls)) {
                              return toBigInteger(value);
                           } else if (BigDecimal.class.equals(cls)) {
                              return toBigDecimal(value);
                           } else {
                              throw new ConversionException("The value '" + value + "' (" + value.getClass() + ")" + " can't be converted to a " + cls.getName() + " object");
                           }
                        } else {
                           return toDouble(value);
                        }
                     } else {
                        return toFloat(value);
                     }
                  } else {
                     return toShort(value);
                  }
               } else {
                  return toByte(value);
               }
            } else {
               return toInteger(value);
            }
         } else {
            return toCharacter(value);
         }
      } else {
         return toBoolean(value);
      }
   }

   public static Boolean toBoolean(Object value) throws ConversionException {
      if (value instanceof Boolean) {
         return (Boolean)value;
      } else if (value instanceof String) {
         Boolean b = BooleanUtils.toBooleanObject((String)value);
         if (b == null) {
            throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
         } else {
            return b;
         }
      } else {
         throw new ConversionException("The value " + value + " can't be converted to a Boolean object");
      }
   }

   public static Character toCharacter(Object value) throws ConversionException {
      String strValue = String.valueOf(value);
      if (strValue.length() == 1) {
         return strValue.charAt(0);
      } else {
         throw new ConversionException(String.format("The value '%s' cannot be converted to a Character object!", strValue));
      }
   }

   public static Byte toByte(Object value) throws ConversionException {
      Number n = toNumber(value, Byte.class);
      return n instanceof Byte ? (Byte)n : new Byte(n.byteValue());
   }

   public static Short toShort(Object value) throws ConversionException {
      Number n = toNumber(value, Short.class);
      return n instanceof Short ? (Short)n : new Short(n.shortValue());
   }

   public static Integer toInteger(Object value) throws ConversionException {
      Number n = toNumber(value, Integer.class);
      return n instanceof Integer ? (Integer)n : new Integer(n.intValue());
   }

   public static Long toLong(Object value) throws ConversionException {
      Number n = toNumber(value, Long.class);
      return n instanceof Long ? (Long)n : new Long(n.longValue());
   }

   public static Float toFloat(Object value) throws ConversionException {
      Number n = toNumber(value, Float.class);
      return n instanceof Float ? (Float)n : new Float(n.floatValue());
   }

   public static Double toDouble(Object value) throws ConversionException {
      Number n = toNumber(value, Double.class);
      return n instanceof Double ? (Double)n : new Double(n.doubleValue());
   }

   public static BigInteger toBigInteger(Object value) throws ConversionException {
      Number n = toNumber(value, BigInteger.class);
      return n instanceof BigInteger ? (BigInteger)n : BigInteger.valueOf(n.longValue());
   }

   public static BigDecimal toBigDecimal(Object value) throws ConversionException {
      Number n = toNumber(value, BigDecimal.class);
      return n instanceof BigDecimal ? (BigDecimal)n : new BigDecimal(n.doubleValue());
   }

   static Number toNumber(Object value, Class targetClass) throws ConversionException {
      if (value instanceof Number) {
         return (Number)value;
      } else {
         String str = value.toString();
         if (str.startsWith("0x")) {
            try {
               return new BigInteger(str.substring("0x".length()), 16);
            } catch (NumberFormatException var4) {
               throw new ConversionException("Could not convert " + str + " to " + targetClass.getName() + "! Invalid hex number.", var4);
            }
         } else if (str.startsWith("0b")) {
            try {
               return new BigInteger(str.substring("0b".length()), 2);
            } catch (NumberFormatException var5) {
               throw new ConversionException("Could not convert " + str + " to " + targetClass.getName() + "! Invalid binary number.", var5);
            }
         } else {
            try {
               Constructor constr = targetClass.getConstructor(CONSTR_ARGS);
               return (Number)constr.newInstance(str);
            } catch (InvocationTargetException var6) {
               throw new ConversionException("Could not convert " + str + " to " + targetClass.getName(), var6.getTargetException());
            } catch (Exception var7) {
               throw new ConversionException("Conversion error when trying to convert " + str + " to " + targetClass.getName(), var7);
            }
         }
      }
   }

   public static URL toURL(Object value) throws ConversionException {
      if (value instanceof URL) {
         return (URL)value;
      } else if (value instanceof String) {
         try {
            return new URL((String)value);
         } catch (MalformedURLException var2) {
            throw new ConversionException("The value " + value + " can't be converted to an URL", var2);
         }
      } else {
         throw new ConversionException("The value " + value + " can't be converted to an URL");
      }
   }

   public static Locale toLocale(Object value) throws ConversionException {
      if (value instanceof Locale) {
         return (Locale)value;
      } else if (!(value instanceof String)) {
         throw new ConversionException("The value " + value + " can't be converted to a Locale");
      } else {
         List elements = split((String)value, '_');
         int size = elements.size();
         if (size < 1 || ((String)elements.get(0)).length() != 2 && ((String)elements.get(0)).length() != 0) {
            throw new ConversionException("The value " + value + " can't be converted to a Locale");
         } else {
            String language = (String)elements.get(0);
            String country = size >= 2 ? (String)elements.get(1) : "";
            String variant = size >= 3 ? (String)elements.get(2) : "";
            return new Locale(language, country, variant);
         }
      }
   }

   public static List split(String s, char delimiter, boolean trim) {
      if (s == null) {
         return new ArrayList();
      } else {
         List list = new ArrayList();
         StringBuilder token = new StringBuilder();
         int begin = 0;

         boolean inEscape;
         for(inEscape = false; begin < s.length(); ++begin) {
            char c = s.charAt(begin);
            if (inEscape) {
               if (c != delimiter && c != '\\') {
                  token.append('\\');
               }

               token.append(c);
               inEscape = false;
            } else if (c == delimiter) {
               String t = token.toString();
               if (trim) {
                  t = t.trim();
               }

               list.add(t);
               token = new StringBuilder();
            } else if (c == '\\') {
               inEscape = true;
            } else {
               token.append(c);
            }
         }

         if (inEscape) {
            token.append('\\');
         }

         String t = token.toString();
         if (trim) {
            t = t.trim();
         }

         list.add(t);
         return list;
      }
   }

   public static List split(String s, char delimiter) {
      return split(s, delimiter, true);
   }

   public static String escapeDelimiters(String s, char delimiter) {
      String s1 = StringUtils.replace(s, LIST_ESCAPE, LIST_ESCAPE + LIST_ESCAPE);
      return escapeListDelimiter(s1, delimiter);
   }

   public static String escapeListDelimiter(String s, char delimiter) {
      return StringUtils.replace(s, String.valueOf(delimiter), LIST_ESCAPE + delimiter);
   }

   public static Color toColor(Object value) throws ConversionException {
      if (value instanceof Color) {
         return (Color)value;
      } else if (value instanceof String && !StringUtils.isBlank((String)value)) {
         String color = ((String)value).trim();
         int[] components = new int[3];
         int minlength = components.length * 2;
         if (color.length() < minlength) {
            throw new ConversionException("The value " + value + " can't be converted to a Color");
         } else {
            if (color.startsWith("#")) {
               color = color.substring(1);
            }

            try {
               int alpha;
               for(alpha = 0; alpha < components.length; ++alpha) {
                  components[alpha] = Integer.parseInt(color.substring(2 * alpha, 2 * alpha + 2), 16);
               }

               if (color.length() >= minlength + 2) {
                  alpha = Integer.parseInt(color.substring(minlength, minlength + 2), 16);
               } else {
                  alpha = Color.black.getAlpha();
               }

               return new Color(components[0], components[1], components[2], alpha);
            } catch (Exception var5) {
               throw new ConversionException("The value " + value + " can't be converted to a Color", var5);
            }
         }
      } else {
         throw new ConversionException("The value " + value + " can't be converted to a Color");
      }
   }

   static InetAddress toInetAddress(Object value) throws ConversionException {
      if (value instanceof InetAddress) {
         return (InetAddress)value;
      } else if (value instanceof String) {
         try {
            return InetAddress.getByName((String)value);
         } catch (UnknownHostException var2) {
            throw new ConversionException("The value " + value + " can't be converted to a InetAddress", var2);
         }
      } else {
         throw new ConversionException("The value " + value + " can't be converted to a InetAddress");
      }
   }

   static Object toInternetAddress(Object value) throws ConversionException {
      if (value.getClass().getName().equals("javax.mail.internet.InternetAddress")) {
         return value;
      } else if (value instanceof String) {
         try {
            Constructor ctor = Class.forName("javax.mail.internet.InternetAddress").getConstructor(String.class);
            return ctor.newInstance(value);
         } catch (Exception var2) {
            throw new ConversionException("The value " + value + " can't be converted to a InternetAddress", var2);
         }
      } else {
         throw new ConversionException("The value " + value + " can't be converted to a InternetAddress");
      }
   }

   static boolean isEnum(Class cls) {
      return cls.isEnum();
   }

   static Enum toEnum(Object value, Class cls) throws ConversionException {
      if (value.getClass().equals(cls)) {
         return (Enum)cls.cast(value);
      } else if (value instanceof String) {
         try {
            return Enum.valueOf(cls, (String)value);
         } catch (Exception var3) {
            throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
         }
      } else if (value instanceof Number) {
         try {
            Enum[] enumConstants = (Enum[])cls.getEnumConstants();
            return enumConstants[((Number)value).intValue()];
         } catch (Exception var4) {
            throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
         }
      } else {
         throw new ConversionException("The value " + value + " can't be converted to a " + cls.getName());
      }
   }

   public static Date toDate(Object value, String format) throws ConversionException {
      if (value instanceof Date) {
         return (Date)value;
      } else if (value instanceof Calendar) {
         return ((Calendar)value).getTime();
      } else if (value instanceof String) {
         try {
            return (new SimpleDateFormat(format)).parse((String)value);
         } catch (ParseException var3) {
            throw new ConversionException("The value " + value + " can't be converted to a Date", var3);
         }
      } else {
         throw new ConversionException("The value " + value + " can't be converted to a Date");
      }
   }

   public static Calendar toCalendar(Object value, String format) throws ConversionException {
      if (value instanceof Calendar) {
         return (Calendar)value;
      } else {
         Calendar calendar;
         if (value instanceof Date) {
            calendar = Calendar.getInstance();
            calendar.setTime((Date)value);
            return calendar;
         } else if (value instanceof String) {
            try {
               calendar = Calendar.getInstance();
               calendar.setTime((new SimpleDateFormat(format)).parse((String)value));
               return calendar;
            } catch (ParseException var3) {
               throw new ConversionException("The value " + value + " can't be converted to a Calendar", var3);
            }
         } else {
            throw new ConversionException("The value " + value + " can't be converted to a Calendar");
         }
      }
   }

   public static Iterator toIterator(Object value, char delimiter) {
      return flatten(value, delimiter).iterator();
   }

   private static Collection flatten(Object value, char delimiter) {
      if (value instanceof String) {
         String s = (String)value;
         if (s.indexOf(delimiter) > 0) {
            return split(s, delimiter);
         }
      }

      Collection result = new LinkedList();
      if (value instanceof Iterable) {
         flattenIterator(result, ((Iterable)value).iterator(), delimiter);
      } else if (value instanceof Iterator) {
         flattenIterator(result, (Iterator)value, delimiter);
      } else if (value != null) {
         if (value.getClass().isArray()) {
            int len = Array.getLength(value);

            for(int idx = 0; idx < len; ++idx) {
               result.addAll(flatten(Array.get(value, idx), delimiter));
            }
         } else {
            result.add(value);
         }
      }

      return result;
   }

   private static void flattenIterator(Collection target, Iterator it, char delimiter) {
      while(it.hasNext()) {
         target.addAll(flatten(it.next(), delimiter));
      }

   }

   public static Object interpolate(Object value, AbstractConfiguration config) {
      return value instanceof String ? config.getSubstitutor().replace((String)value) : value;
   }

   private static Object convertToEnum(Class enumClass, Object value) {
      return toEnum(value, enumClass.asSubclass(Enum.class));
   }
}
