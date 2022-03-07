package com.sun.syndication.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

public class PropertiesLoader {
   private static final String MASTER_PLUGIN_FILE = "com/sun/syndication/rome.properties";
   private static final String EXTRA_PLUGIN_FILE = "rome.properties";
   private static Map clMap = new WeakHashMap();
   private Properties[] _properties;

   public static PropertiesLoader getPropertiesLoader() {
      synchronized(PropertiesLoader.class) {
         PropertiesLoader loader = (PropertiesLoader)clMap.get(Thread.currentThread().getContextClassLoader());
         if (loader == null) {
            try {
               loader = new PropertiesLoader("com/sun/syndication/rome.properties", "rome.properties");
               clMap.put(Thread.currentThread().getContextClassLoader(), loader);
            } catch (IOException var4) {
               throw new RuntimeException(var4);
            }
         }

         return loader;
      }
   }

   private PropertiesLoader(String masterFileLocation, String extraFileLocation) throws IOException {
      List propertiesList = new ArrayList();
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      try {
         InputStream is = classLoader.getResourceAsStream(masterFileLocation);
         Properties p = new Properties();
         p.load(is);
         is.close();
         propertiesList.add(p);
      } catch (IOException var11) {
         IOException ex = new IOException("could not load ROME master plugins file [" + masterFileLocation + "], " + var11.getMessage());
         ex.setStackTrace(var11.getStackTrace());
         throw ex;
      }

      Properties p;
      for(Enumeration urls = classLoader.getResources(extraFileLocation); urls.hasMoreElements(); propertiesList.add(p)) {
         URL url = (URL)urls.nextElement();
         p = new Properties();

         try {
            InputStream is = url.openStream();
            p.load(is);
            is.close();
         } catch (IOException var10) {
            IOException ex = new IOException("could not load ROME extensions plugins file [" + url.toString() + "], " + var10.getMessage());
            ex.setStackTrace(var10.getStackTrace());
            throw ex;
         }
      }

      this._properties = new Properties[propertiesList.size()];
      propertiesList.toArray(this._properties);
   }

   public String[] getTokenizedProperty(String key, String separator) {
      List entriesList = new ArrayList();

      for(int i = 0; i < this._properties.length; ++i) {
         String values = this._properties[i].getProperty(key);
         if (values != null) {
            StringTokenizer st = new StringTokenizer(values, separator);

            while(st.hasMoreTokens()) {
               String token = st.nextToken();
               entriesList.add(token);
            }
         }
      }

      String[] entries = new String[entriesList.size()];
      entriesList.toArray(entries);
      return entries;
   }

   public String[] getProperty(String key) {
      List entriesList = new ArrayList();

      for(int i = 0; i < this._properties.length; ++i) {
         String values = this._properties[i].getProperty(key);
         if (values != null) {
            entriesList.add(values);
         }
      }

      String[] entries = new String[entriesList.size()];
      entriesList.toArray(entries);
      return entries;
   }
}
