package org.apache.commons.configuration;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import org.apache.commons.configuration.event.ConfigurationErrorEvent;
import org.apache.commons.configuration.event.ConfigurationErrorListener;
import org.apache.commons.configuration.event.EventSource;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.NoOpLog;

public abstract class AbstractConfiguration extends EventSource implements Configuration {
   public static final int EVENT_ADD_PROPERTY = 1;
   public static final int EVENT_CLEAR_PROPERTY = 2;
   public static final int EVENT_SET_PROPERTY = 3;
   public static final int EVENT_CLEAR = 4;
   public static final int EVENT_READ_PROPERTY = 5;
   protected static final String START_TOKEN = "${";
   protected static final String END_TOKEN = "}";
   private static final char DISABLED_DELIMITER = '\u0000';
   private static char defaultListDelimiter = ',';
   private char listDelimiter;
   private boolean delimiterParsingDisabled;
   private boolean throwExceptionOnMissing;
   private StrSubstitutor substitutor;
   private Log log;

   public AbstractConfiguration() {
      this.listDelimiter = defaultListDelimiter;
      this.setLogger((Log)null);
   }

   public static void setDefaultListDelimiter(char delimiter) {
      defaultListDelimiter = delimiter;
   }

   /** @deprecated */
   @Deprecated
   public static void setDelimiter(char delimiter) {
      setDefaultListDelimiter(delimiter);
   }

   public static char getDefaultListDelimiter() {
      return defaultListDelimiter;
   }

   /** @deprecated */
   @Deprecated
   public static char getDelimiter() {
      return getDefaultListDelimiter();
   }

   public void setListDelimiter(char listDelimiter) {
      this.listDelimiter = listDelimiter;
   }

   public char getListDelimiter() {
      return this.listDelimiter;
   }

   public boolean isDelimiterParsingDisabled() {
      return this.delimiterParsingDisabled;
   }

   public void setDelimiterParsingDisabled(boolean delimiterParsingDisabled) {
      this.delimiterParsingDisabled = delimiterParsingDisabled;
   }

   public void setThrowExceptionOnMissing(boolean throwExceptionOnMissing) {
      this.throwExceptionOnMissing = throwExceptionOnMissing;
   }

   public boolean isThrowExceptionOnMissing() {
      return this.throwExceptionOnMissing;
   }

   public synchronized StrSubstitutor getSubstitutor() {
      if (this.substitutor == null) {
         this.substitutor = new StrSubstitutor(this.createInterpolator());
      }

      return this.substitutor;
   }

   public ConfigurationInterpolator getInterpolator() {
      return (ConfigurationInterpolator)this.getSubstitutor().getVariableResolver();
   }

   protected ConfigurationInterpolator createInterpolator() {
      ConfigurationInterpolator interpol = new ConfigurationInterpolator();
      interpol.setDefaultLookup(new StrLookup() {
         public String lookup(String var) {
            Object prop = AbstractConfiguration.this.resolveContainerStore(var);
            return prop != null ? prop.toString() : null;
         }
      });
      return interpol;
   }

   public Log getLogger() {
      return this.log;
   }

   public void setLogger(Log log) {
      this.log = (Log)(log != null ? log : new NoOpLog());
   }

   public void addErrorLogListener() {
      this.addErrorListener(new ConfigurationErrorListener() {
         public void configurationError(ConfigurationErrorEvent event) {
            AbstractConfiguration.this.getLogger().warn("Internal error", event.getCause());
         }
      });
   }

   public void addProperty(String key, Object value) {
      this.fireEvent(1, key, value, true);
      this.addPropertyValues(key, value, this.isDelimiterParsingDisabled() ? '\u0000' : this.getListDelimiter());
      this.fireEvent(1, key, value, false);
   }

   protected abstract void addPropertyDirect(String var1, Object var2);

   private void addPropertyValues(String key, Object value, char delimiter) {
      Iterator it = PropertyConverter.toIterator(value, delimiter);

      while(it.hasNext()) {
         this.addPropertyDirect(key, it.next());
      }

   }

   protected String interpolate(String base) {
      Object result = this.interpolate((Object)base);
      return result == null ? null : result.toString();
   }

   protected Object interpolate(Object value) {
      return PropertyConverter.interpolate(value, this);
   }

   /** @deprecated */
   @Deprecated
   protected String interpolateHelper(String base, List priorVariables) {
      return base;
   }

   public Configuration subset(String prefix) {
      return new SubsetConfiguration(this, prefix, ".");
   }

   public void setProperty(String key, Object value) {
      this.fireEvent(3, key, value, true);
      this.setDetailEvents(false);

      try {
         this.clearProperty(key);
         this.addProperty(key, value);
      } finally {
         this.setDetailEvents(true);
      }

      this.fireEvent(3, key, value, false);
   }

   public void clearProperty(String key) {
      this.fireEvent(2, key, (Object)null, true);
      this.clearPropertyDirect(key);
      this.fireEvent(2, key, (Object)null, false);
   }

   protected void clearPropertyDirect(String key) {
   }

   public void clear() {
      this.fireEvent(4, (String)null, (Object)null, true);
      this.setDetailEvents(false);
      boolean useIterator = true;

      try {
         Iterator it = this.getKeys();

         while(it.hasNext()) {
            String key = (String)it.next();
            if (useIterator) {
               try {
                  it.remove();
               } catch (UnsupportedOperationException var8) {
                  useIterator = false;
               }
            }

            if (useIterator && this.containsKey(key)) {
               useIterator = false;
            }

            if (!useIterator) {
               this.clearProperty(key);
            }
         }
      } finally {
         this.setDetailEvents(true);
      }

      this.fireEvent(4, (String)null, (Object)null, false);
   }

   public Iterator getKeys(String prefix) {
      return new PrefixedKeysIterator(this.getKeys(), prefix);
   }

   public Properties getProperties(String key) {
      return this.getProperties(key, (Properties)null);
   }

   public Properties getProperties(String key, Properties defaults) {
      String[] tokens = this.getStringArray(key);
      Properties props = defaults == null ? new Properties() : new Properties(defaults);
      String[] arr$ = tokens;
      int len$ = tokens.length;
      int i$ = 0;

      while(true) {
         if (i$ < len$) {
            String token = arr$[i$];
            int equalSign = token.indexOf(61);
            if (equalSign > 0) {
               String pkey = token.substring(0, equalSign).trim();
               String pvalue = token.substring(equalSign + 1).trim();
               props.put(pkey, pvalue);
               ++i$;
               continue;
            }

            if (tokens.length != 1 || !"".equals(token)) {
               throw new IllegalArgumentException('\'' + token + "' does not contain an equals sign");
            }
         }

         return props;
      }
   }

   public boolean getBoolean(String key) {
      Boolean b = this.getBoolean(key, (Boolean)null);
      if (b != null) {
         return b;
      } else {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      }
   }

   public boolean getBoolean(String key, boolean defaultValue) {
      return this.getBoolean(key, BooleanUtils.toBooleanObject(defaultValue));
   }

   public Boolean getBoolean(String key, Boolean defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toBoolean(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a Boolean object", var5);
         }
      }
   }

   public byte getByte(String key) {
      Byte b = this.getByte(key, (Byte)null);
      if (b != null) {
         return b;
      } else {
         throw new NoSuchElementException('\'' + key + " doesn't map to an existing object");
      }
   }

   public byte getByte(String key, byte defaultValue) {
      return this.getByte(key, new Byte(defaultValue));
   }

   public Byte getByte(String key, Byte defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toByte(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a Byte object", var5);
         }
      }
   }

   public double getDouble(String key) {
      Double d = this.getDouble(key, (Double)null);
      if (d != null) {
         return d;
      } else {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      }
   }

   public double getDouble(String key, double defaultValue) {
      return this.getDouble(key, new Double(defaultValue));
   }

   public Double getDouble(String key, Double defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toDouble(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a Double object", var5);
         }
      }
   }

   public float getFloat(String key) {
      Float f = this.getFloat(key, (Float)null);
      if (f != null) {
         return f;
      } else {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      }
   }

   public float getFloat(String key, float defaultValue) {
      return this.getFloat(key, new Float(defaultValue));
   }

   public Float getFloat(String key, Float defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toFloat(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a Float object", var5);
         }
      }
   }

   public int getInt(String key) {
      Integer i = this.getInteger(key, (Integer)null);
      if (i != null) {
         return i;
      } else {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      }
   }

   public int getInt(String key, int defaultValue) {
      Integer i = this.getInteger(key, (Integer)null);
      return i == null ? defaultValue : i;
   }

   public Integer getInteger(String key, Integer defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toInteger(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to an Integer object", var5);
         }
      }
   }

   public long getLong(String key) {
      Long l = this.getLong(key, (Long)null);
      if (l != null) {
         return l;
      } else {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      }
   }

   public long getLong(String key, long defaultValue) {
      return this.getLong(key, new Long(defaultValue));
   }

   public Long getLong(String key, Long defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toLong(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a Long object", var5);
         }
      }
   }

   public short getShort(String key) {
      Short s = this.getShort(key, (Short)null);
      if (s != null) {
         return s;
      } else {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      }
   }

   public short getShort(String key, short defaultValue) {
      return this.getShort(key, new Short(defaultValue));
   }

   public Short getShort(String key, Short defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toShort(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a Short object", var5);
         }
      }
   }

   public BigDecimal getBigDecimal(String key) {
      BigDecimal number = this.getBigDecimal(key, (BigDecimal)null);
      if (number != null) {
         return number;
      } else if (this.isThrowExceptionOnMissing()) {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      } else {
         return null;
      }
   }

   public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toBigDecimal(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a BigDecimal object", var5);
         }
      }
   }

   public BigInteger getBigInteger(String key) {
      BigInteger number = this.getBigInteger(key, (BigInteger)null);
      if (number != null) {
         return number;
      } else if (this.isThrowExceptionOnMissing()) {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      } else {
         return null;
      }
   }

   public BigInteger getBigInteger(String key, BigInteger defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value == null) {
         return defaultValue;
      } else {
         try {
            return PropertyConverter.toBigInteger(this.interpolate(value));
         } catch (ConversionException var5) {
            throw new ConversionException('\'' + key + "' doesn't map to a BigInteger object", var5);
         }
      }
   }

   public String getString(String key) {
      String s = this.getString(key, (String)null);
      if (s != null) {
         return s;
      } else if (this.isThrowExceptionOnMissing()) {
         throw new NoSuchElementException('\'' + key + "' doesn't map to an existing object");
      } else {
         return null;
      }
   }

   public String getString(String key, String defaultValue) {
      Object value = this.resolveContainerStore(key);
      if (value instanceof String) {
         return this.interpolate((String)value);
      } else if (value == null) {
         return this.interpolate(defaultValue);
      } else {
         throw new ConversionException('\'' + key + "' doesn't map to a String object");
      }
   }

   public String[] getStringArray(String key) {
      Object value = this.getProperty(key);
      String[] array;
      if (value instanceof String) {
         array = new String[]{this.interpolate((String)value)};
      } else if (value instanceof List) {
         List list = (List)value;
         array = new String[list.size()];

         for(int i = 0; i < array.length; ++i) {
            array[i] = this.interpolate(ObjectUtils.toString(list.get(i), (String)null));
         }
      } else if (value == null) {
         array = new String[0];
      } else {
         if (!this.isScalarValue(value)) {
            throw new ConversionException('\'' + key + "' doesn't map to a String/List object");
         }

         array = new String[]{value.toString()};
      }

      return array;
   }

   public List getList(String key) {
      return this.getList(key, new ArrayList());
   }

   public List getList(String key, List defaultValue) {
      Object value = this.getProperty(key);
      Object list;
      if (value instanceof String) {
         list = new ArrayList(1);
         ((List)list).add(this.interpolate((String)value));
      } else if (value instanceof List) {
         list = new ArrayList();
         List l = (List)value;
         Iterator i$ = l.iterator();

         while(i$.hasNext()) {
            Object elem = i$.next();
            ((List)list).add(this.interpolate(elem));
         }
      } else {
         if (value != null) {
            if (value.getClass().isArray()) {
               return Arrays.asList((Object[])((Object[])value));
            }

            if (this.isScalarValue(value)) {
               return Collections.singletonList(value.toString());
            }

            throw new ConversionException('\'' + key + "' doesn't map to a List object: " + value + ", a " + value.getClass().getName());
         }

         list = defaultValue;
      }

      return (List)list;
   }

   protected Object resolveContainerStore(String key) {
      Object value = this.getProperty(key);
      if (value != null) {
         if (value instanceof Collection) {
            Collection collection = (Collection)value;
            value = collection.isEmpty() ? null : collection.iterator().next();
         } else if (value.getClass().isArray() && Array.getLength(value) > 0) {
            value = Array.get(value, 0);
         }
      }

      return value;
   }

   protected boolean isScalarValue(Object value) {
      return ClassUtils.wrapperToPrimitive(value.getClass()) != null;
   }

   public void copy(Configuration c) {
      String key;
      Object value;
      if (c != null) {
         for(Iterator it = c.getKeys(); it.hasNext(); this.fireEvent(3, key, value, false)) {
            key = (String)it.next();
            value = c.getProperty(key);
            this.fireEvent(3, key, value, true);
            this.setDetailEvents(false);

            try {
               this.clearProperty(key);
               this.addPropertyValues(key, value, '\u0000');
            } finally {
               this.setDetailEvents(true);
            }
         }
      }

   }

   public void append(Configuration c) {
      if (c != null) {
         Iterator it = c.getKeys();

         while(it.hasNext()) {
            String key = (String)it.next();
            Object value = c.getProperty(key);
            this.fireEvent(1, key, value, true);
            this.addPropertyValues(key, value, '\u0000');
            this.fireEvent(1, key, value, false);
         }
      }

   }

   public Configuration interpolatedConfiguration() {
      AbstractConfiguration c = (AbstractConfiguration)ConfigurationUtils.cloneConfiguration(this);
      c.setDelimiterParsingDisabled(true);
      Iterator it = this.getKeys();

      while(it.hasNext()) {
         String key = (String)it.next();
         c.setProperty(key, this.getList(key));
      }

      c.setDelimiterParsingDisabled(this.isDelimiterParsingDisabled());
      return c;
   }
}
