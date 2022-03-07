package org.apache.commons.configuration;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

public class DataConfiguration extends AbstractConfiguration implements Serializable {
   public static final String DATE_FORMAT_KEY = "org.apache.commons.configuration.format.date";
   public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
   private static final long serialVersionUID = -69011336405718640L;
   protected Configuration configuration;

   public DataConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public Object getProperty(String key) {
      return this.configuration.getProperty(key);
   }

   protected void addPropertyDirect(String key, Object obj) {
      if (this.configuration instanceof AbstractConfiguration) {
         ((AbstractConfiguration)this.configuration).addPropertyDirect(key, obj);
      } else {
         this.configuration.addProperty(key, obj);
      }

   }

   public void addProperty(String key, Object value) {
      this.getConfiguration().addProperty(key, value);
   }

   public boolean isEmpty() {
      return this.configuration.isEmpty();
   }

   public boolean containsKey(String key) {
      return this.configuration.containsKey(key);
   }

   public void clearProperty(String key) {
      this.configuration.clearProperty(key);
   }

   public void setProperty(String key, Object value) {
      this.configuration.setProperty(key, value);
   }

   public Iterator getKeys() {
      return this.configuration.getKeys();
   }

   public Object get(Class cls, String key) {
      Object value = this.get(cls, key, (Object)null);
      if (value != null) {
         return value;
      } else if (this.isThrowExceptionOnMissing()) {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      } else {
         return null;
      }
   }

   public Object get(Class cls, String key, Object defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         return !Date.class.equals(cls) && !Calendar.class.equals(cls) ? convert(cls, key, this.interpolate(value), (Object[])null) : convert(cls, key, this.interpolate(value), new String[]{this.getDefaultDateFormat()});
      }
   }

   public List getList(Class cls, String key) {
      return this.getList(cls, key, new ArrayList());
   }

   public List getList(Class cls, String key, List defaultValue) {
      Object value = this.getProperty(key);
      Class valueClass = value != null ? value.getClass() : null;
      Object list;
      if (value != null && (!(value instanceof String) || !StringUtils.isEmpty((String)value))) {
         list = new ArrayList();
         Object[] params = null;
         if (cls.equals(Date.class) || cls.equals(Calendar.class)) {
            params = new Object[]{this.getDefaultDateFormat()};
         }

         if (valueClass.isArray()) {
            Class arrayType = valueClass.getComponentType();
            int length = Array.getLength(value);
            int i;
            if (!arrayType.equals(cls) && (!arrayType.isPrimitive() || !cls.equals(ClassUtils.primitiveToWrapper(arrayType)))) {
               for(i = 0; i < length; ++i) {
                  ((List)list).add(convert(cls, key, this.interpolate(Array.get(value, i)), params));
               }
            } else {
               for(i = 0; i < length; ++i) {
                  ((List)list).add(cls.cast(Array.get(value, i)));
               }
            }
         } else if (value instanceof Collection) {
            Collection values = (Collection)value;
            Iterator i$ = values.iterator();

            while(i$.hasNext()) {
               Object o = i$.next();
               ((List)list).add(convert(cls, key, this.interpolate(o), params));
            }
         } else {
            ((List)list).add(convert(cls, key, this.interpolate(value), params));
         }
      } else {
         list = defaultValue;
      }

      return (List)list;
   }

   public Object getArray(Class cls, String key) {
      return this.getArray(cls, key, Array.newInstance(cls, 0));
   }

   public Object getArray(Class cls, String key, Object defaultValue) {
      if (defaultValue != null && (!defaultValue.getClass().isArray() || !cls.isAssignableFrom(defaultValue.getClass().getComponentType()))) {
         throw new IllegalArgumentException("The type of the default value (" + defaultValue.getClass() + ")" + " is not an array of the specified class (" + cls + ")");
      } else if (cls.isPrimitive()) {
         return this.getPrimitiveArray(cls, key, defaultValue);
      } else {
         List list = this.getList(cls, key);
         return list.isEmpty() ? defaultValue : list.toArray((Object[])((Object[])Array.newInstance(cls, list.size())));
      }
   }

   private Object getPrimitiveArray(Class cls, String key, Object defaultValue) {
      Object value = this.getProperty(key);
      Class valueClass = value != null ? value.getClass() : null;
      Object array;
      if (value != null && (!(value instanceof String) || !StringUtils.isEmpty((String)value))) {
         int length;
         if (valueClass.isArray()) {
            Class arrayType = valueClass.getComponentType();
            length = Array.getLength(value);
            if (arrayType.equals(cls)) {
               array = value;
            } else {
               if (!arrayType.equals(ClassUtils.primitiveToWrapper(cls))) {
                  throw new ConversionException('\'' + key + "' (" + arrayType + ")" + " doesn't map to a compatible array of " + cls);
               }

               array = Array.newInstance(cls, length);

               for(int i = 0; i < length; ++i) {
                  Array.set(array, i, Array.get(value, i));
               }
            }
         } else if (value instanceof Collection) {
            Collection values = (Collection)value;
            array = Array.newInstance(cls, values.size());
            length = 0;
            Iterator i$ = values.iterator();

            while(i$.hasNext()) {
               Object o = i$.next();
               Object convertedValue = convert(ClassUtils.primitiveToWrapper(cls), key, this.interpolate(o), (Object[])null);
               Array.set(array, length++, convertedValue);
            }
         } else {
            Object convertedValue = convert(ClassUtils.primitiveToWrapper(cls), key, this.interpolate(value), (Object[])null);
            array = Array.newInstance(cls, 1);
            Array.set(array, 0, convertedValue);
         }
      } else {
         array = defaultValue;
      }

      return array;
   }

   public List getBooleanList(String key) {
      return this.getBooleanList(key, new ArrayList());
   }

   public List getBooleanList(String key, List defaultValue) {
      return this.getList(Boolean.class, key, defaultValue);
   }

   public boolean[] getBooleanArray(String key) {
      return (boolean[])((boolean[])this.getArray(Boolean.TYPE, key));
   }

   public boolean[] getBooleanArray(String key, boolean[] defaultValue) {
      return (boolean[])((boolean[])this.getArray(Boolean.TYPE, key, defaultValue));
   }

   public List getByteList(String key) {
      return this.getByteList(key, new ArrayList());
   }

   public List getByteList(String key, List defaultValue) {
      return this.getList(Byte.class, key, defaultValue);
   }

   public byte[] getByteArray(String key) {
      return this.getByteArray(key, new byte[0]);
   }

   public byte[] getByteArray(String key, byte[] defaultValue) {
      return (byte[])((byte[])this.getArray(Byte.TYPE, key, defaultValue));
   }

   public List getShortList(String key) {
      return this.getShortList(key, new ArrayList());
   }

   public List getShortList(String key, List defaultValue) {
      return this.getList(Short.class, key, defaultValue);
   }

   public short[] getShortArray(String key) {
      return this.getShortArray(key, new short[0]);
   }

   public short[] getShortArray(String key, short[] defaultValue) {
      return (short[])((short[])this.getArray(Short.TYPE, key, defaultValue));
   }

   public List getIntegerList(String key) {
      return this.getIntegerList(key, new ArrayList());
   }

   public List getIntegerList(String key, List defaultValue) {
      return this.getList(Integer.class, key, defaultValue);
   }

   public int[] getIntArray(String key) {
      return this.getIntArray(key, new int[0]);
   }

   public int[] getIntArray(String key, int[] defaultValue) {
      return (int[])((int[])this.getArray(Integer.TYPE, key, defaultValue));
   }

   public List getLongList(String key) {
      return this.getLongList(key, new ArrayList());
   }

   public List getLongList(String key, List defaultValue) {
      return this.getList(Long.class, key, defaultValue);
   }

   public long[] getLongArray(String key) {
      return this.getLongArray(key, new long[0]);
   }

   public long[] getLongArray(String key, long[] defaultValue) {
      return (long[])((long[])this.getArray(Long.TYPE, key, defaultValue));
   }

   public List getFloatList(String key) {
      return this.getFloatList(key, new ArrayList());
   }

   public List getFloatList(String key, List defaultValue) {
      return this.getList(Float.class, key, defaultValue);
   }

   public float[] getFloatArray(String key) {
      return this.getFloatArray(key, new float[0]);
   }

   public float[] getFloatArray(String key, float[] defaultValue) {
      return (float[])((float[])this.getArray(Float.TYPE, key, defaultValue));
   }

   public List getDoubleList(String key) {
      return this.getDoubleList(key, new ArrayList());
   }

   public List getDoubleList(String key, List defaultValue) {
      return this.getList(Double.class, key, defaultValue);
   }

   public double[] getDoubleArray(String key) {
      return this.getDoubleArray(key, new double[0]);
   }

   public double[] getDoubleArray(String key, double[] defaultValue) {
      return (double[])((double[])this.getArray(Double.TYPE, key, defaultValue));
   }

   public List getBigIntegerList(String key) {
      return this.getBigIntegerList(key, new ArrayList());
   }

   public List getBigIntegerList(String key, List defaultValue) {
      return this.getList(BigInteger.class, key, defaultValue);
   }

   public BigInteger[] getBigIntegerArray(String key) {
      return this.getBigIntegerArray(key, new BigInteger[0]);
   }

   public BigInteger[] getBigIntegerArray(String key, BigInteger[] defaultValue) {
      return (BigInteger[])((BigInteger[])this.getArray(BigInteger.class, key, defaultValue));
   }

   public List getBigDecimalList(String key) {
      return this.getBigDecimalList(key, new ArrayList());
   }

   public List getBigDecimalList(String key, List defaultValue) {
      return this.getList(BigDecimal.class, key, defaultValue);
   }

   public BigDecimal[] getBigDecimalArray(String key) {
      return this.getBigDecimalArray(key, new BigDecimal[0]);
   }

   public BigDecimal[] getBigDecimalArray(String key, BigDecimal[] defaultValue) {
      return (BigDecimal[])((BigDecimal[])this.getArray(BigDecimal.class, key, defaultValue));
   }

   public URL getURL(String key) {
      return (URL)this.get(URL.class, key);
   }

   public URL getURL(String key, URL defaultValue) {
      return (URL)this.get(URL.class, key, defaultValue);
   }

   public List getURLList(String key) {
      return this.getURLList(key, new ArrayList());
   }

   public List getURLList(String key, List defaultValue) {
      return this.getList(URL.class, key, defaultValue);
   }

   public URL[] getURLArray(String key) {
      return this.getURLArray(key, new URL[0]);
   }

   public URL[] getURLArray(String key, URL[] defaultValue) {
      return (URL[])((URL[])this.getArray(URL.class, key, defaultValue));
   }

   public Date getDate(String key) {
      return (Date)this.get(Date.class, key);
   }

   public Date getDate(String key, String format) {
      Date value = this.getDate(key, (Date)null, format);
      if (value != null) {
         return value;
      } else if (this.isThrowExceptionOnMissing()) {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      } else {
         return null;
      }
   }

   public Date getDate(String key, Date defaultValue) {
      return this.getDate(key, defaultValue, this.getDefaultDateFormat());
   }

   public Date getDate(String key, Date defaultValue, String format) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toDate(this.interpolate(value), format);
         } catch (ConversionException var6) {
            throw new ConversionException('\'' + key + "' doesn't map to a Date", var6);
         }
      }
   }

   public List getDateList(String key) {
      return this.getDateList(key, (List)(new ArrayList()));
   }

   public List getDateList(String key, String format) {
      return this.getDateList(key, new ArrayList(), format);
   }

   public List getDateList(String key, List defaultValue) {
      return this.getDateList(key, defaultValue, this.getDefaultDateFormat());
   }

   public List getDateList(String key, List defaultValue, String format) {
      Object value = this.getProperty(key);
      Object list;
      if (value != null && (!(value instanceof String) || !StringUtils.isEmpty((String)value))) {
         if (value.getClass().isArray()) {
            list = new ArrayList();
            int length = Array.getLength(value);

            for(int i = 0; i < length; ++i) {
               ((List)list).add(convert(Date.class, key, this.interpolate(Array.get(value, i)), new String[]{format}));
            }
         } else if (value instanceof Collection) {
            Collection values = (Collection)value;
            list = new ArrayList();
            Iterator i$ = values.iterator();

            while(i$.hasNext()) {
               Object o = i$.next();
               ((List)list).add(convert(Date.class, key, this.interpolate(o), new String[]{format}));
            }
         } else {
            list = new ArrayList();
            ((List)list).add(convert(Date.class, key, this.interpolate(value), new String[]{format}));
         }
      } else {
         list = defaultValue;
      }

      return (List)list;
   }

   public Date[] getDateArray(String key) {
      return this.getDateArray(key, new Date[0]);
   }

   public Date[] getDateArray(String key, String format) {
      return this.getDateArray(key, new Date[0], format);
   }

   public Date[] getDateArray(String key, Date[] defaultValue) {
      return this.getDateArray(key, defaultValue, this.getDefaultDateFormat());
   }

   public Date[] getDateArray(String key, Date[] defaultValue, String format) {
      List list = this.getDateList(key, format);
      return list.isEmpty() ? defaultValue : (Date[])list.toArray(new Date[list.size()]);
   }

   public Calendar getCalendar(String key) {
      return (Calendar)this.get(Calendar.class, key);
   }

   public Calendar getCalendar(String key, String format) {
      Calendar value = this.getCalendar(key, (Calendar)null, format);
      if (value != null) {
         return value;
      } else if (this.isThrowExceptionOnMissing()) {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      } else {
         return null;
      }
   }

   public Calendar getCalendar(String key, Calendar defaultValue) {
      return this.getCalendar(key, defaultValue, this.getDefaultDateFormat());
   }

   public Calendar getCalendar(String key, Calendar defaultValue, String format) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toCalendar(this.interpolate(value), format);
         } catch (ConversionException var6) {
            throw new ConversionException('\'' + key + "' doesn't map to a Calendar", var6);
         }
      }
   }

   public List getCalendarList(String key) {
      return this.getCalendarList(key, (List)(new ArrayList()));
   }

   public List getCalendarList(String key, String format) {
      return this.getCalendarList(key, new ArrayList(), format);
   }

   public List getCalendarList(String key, List defaultValue) {
      return this.getCalendarList(key, defaultValue, this.getDefaultDateFormat());
   }

   public List getCalendarList(String key, List defaultValue, String format) {
      Object value = this.getProperty(key);
      Object list;
      if (value != null && (!(value instanceof String) || !StringUtils.isEmpty((String)value))) {
         if (value.getClass().isArray()) {
            list = new ArrayList();
            int length = Array.getLength(value);

            for(int i = 0; i < length; ++i) {
               ((List)list).add(convert(Calendar.class, key, this.interpolate(Array.get(value, i)), new String[]{format}));
            }
         } else if (value instanceof Collection) {
            Collection values = (Collection)value;
            list = new ArrayList();
            Iterator i$ = values.iterator();

            while(i$.hasNext()) {
               Object o = i$.next();
               ((List)list).add(convert(Calendar.class, key, this.interpolate(o), new String[]{format}));
            }
         } else {
            list = new ArrayList();
            ((List)list).add(convert(Calendar.class, key, this.interpolate(value), new String[]{format}));
         }
      } else {
         list = defaultValue;
      }

      return (List)list;
   }

   public Calendar[] getCalendarArray(String key) {
      return this.getCalendarArray(key, new Calendar[0]);
   }

   public Calendar[] getCalendarArray(String key, String format) {
      return this.getCalendarArray(key, new Calendar[0], format);
   }

   public Calendar[] getCalendarArray(String key, Calendar[] defaultValue) {
      return this.getCalendarArray(key, defaultValue, this.getDefaultDateFormat());
   }

   public Calendar[] getCalendarArray(String key, Calendar[] defaultValue, String format) {
      List list = this.getCalendarList(key, format);
      return list.isEmpty() ? defaultValue : (Calendar[])list.toArray(new Calendar[list.size()]);
   }

   private String getDefaultDateFormat() {
      return this.getString("org.apache.commons.configuration.format.date", "yyyy-MM-dd HH:mm:ss");
   }

   public Locale getLocale(String key) {
      return (Locale)this.get(Locale.class, key);
   }

   public Locale getLocale(String key, Locale defaultValue) {
      return (Locale)this.get(Locale.class, key, defaultValue);
   }

   public List getLocaleList(String key) {
      return this.getLocaleList(key, new ArrayList());
   }

   public List getLocaleList(String key, List defaultValue) {
      return this.getList(Locale.class, key, defaultValue);
   }

   public Locale[] getLocaleArray(String key) {
      return this.getLocaleArray(key, new Locale[0]);
   }

   public Locale[] getLocaleArray(String key, Locale[] defaultValue) {
      return (Locale[])((Locale[])this.getArray(Locale.class, key, defaultValue));
   }

   public Color getColor(String key) {
      return (Color)this.get(Color.class, key);
   }

   public Color getColor(String key, Color defaultValue) {
      return (Color)this.get(Color.class, key, defaultValue);
   }

   public List getColorList(String key) {
      return this.getColorList(key, new ArrayList());
   }

   public List getColorList(String key, List defaultValue) {
      return this.getList(Color.class, key, defaultValue);
   }

   public Color[] getColorArray(String key) {
      return this.getColorArray(key, new Color[0]);
   }

   public Color[] getColorArray(String key, Color[] defaultValue) {
      return (Color[])((Color[])this.getArray(Color.class, key, defaultValue));
   }

   private static Object convert(Class cls, String key, Object value, Object[] params) {
      try {
         Object result = PropertyConverter.to(cls, value, params);
         return cls.cast(result);
      } catch (ConversionException var5) {
         throw new ConversionException('\'' + key + "' doesn't map to a " + cls, var5);
      }
   }
}
