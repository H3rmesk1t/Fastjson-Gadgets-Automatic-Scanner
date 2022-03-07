package com.sun.syndication.io.impl;

import com.sun.syndication.io.DelegatingModuleGenerator;
import com.sun.syndication.io.DelegatingModuleParser;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.WireFeedParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class PluginManager {
   private String[] _propertyValues;
   private Map _pluginsMap;
   private List _pluginsList;
   private List _keys;
   private WireFeedParser _parentParser;
   private WireFeedGenerator _parentGenerator;

   protected PluginManager(String propertyKey) {
      this(propertyKey, (WireFeedParser)null, (WireFeedGenerator)null);
   }

   protected PluginManager(String propertyKey, WireFeedParser parentParser, WireFeedGenerator parentGenerator) {
      this._parentParser = parentParser;
      this._parentGenerator = parentGenerator;
      this._propertyValues = PropertiesLoader.getPropertiesLoader().getTokenizedProperty(propertyKey, ", ");
      this.loadPlugins();
      this._pluginsMap = Collections.unmodifiableMap(this._pluginsMap);
      this._pluginsList = Collections.unmodifiableList(this._pluginsList);
      this._keys = Collections.unmodifiableList(new ArrayList(this._pluginsMap.keySet()));
   }

   protected abstract String getKey(Object var1);

   protected List getKeys() {
      return this._keys;
   }

   protected List getPlugins() {
      return this._pluginsList;
   }

   protected Map getPluginMap() {
      return this._pluginsMap;
   }

   protected Object getPlugin(String key) {
      return this._pluginsMap.get(key);
   }

   private void loadPlugins() {
      List finalPluginsList = new ArrayList();
      this._pluginsList = new ArrayList();
      this._pluginsMap = new HashMap();
      String className = null;

      try {
         Class[] classes = this.getClasses();

         Object plugin;
         for(int i = 0; i < classes.length; ++i) {
            className = classes[i].getName();
            plugin = classes[i].newInstance();
            if (plugin instanceof DelegatingModuleParser) {
               ((DelegatingModuleParser)plugin).setFeedParser(this._parentParser);
            }

            if (plugin instanceof DelegatingModuleGenerator) {
               ((DelegatingModuleGenerator)plugin).setFeedGenerator(this._parentGenerator);
            }

            this._pluginsMap.put(this.getKey(plugin), plugin);
            this._pluginsList.add(plugin);
         }

         Iterator i = this._pluginsMap.values().iterator();

         while(i.hasNext()) {
            finalPluginsList.add(i.next());
         }

         i = this._pluginsList.iterator();

         while(i.hasNext()) {
            plugin = i.next();
            if (!finalPluginsList.contains(plugin)) {
               i.remove();
            }
         }

      } catch (Exception var6) {
         throw new RuntimeException("could not instantiate plugin " + className, var6);
      } catch (ExceptionInInitializerError var7) {
         throw new RuntimeException("could not instantiate plugin " + className, var7);
      }
   }

   private Class[] getClasses() throws ClassNotFoundException {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      List classes = new ArrayList();
      boolean useLoadClass = Boolean.valueOf(System.getProperty("rome.pluginmanager.useloadclass", "false"));

      for(int i = 0; i < this._propertyValues.length; ++i) {
         Class mClass = useLoadClass ? classLoader.loadClass(this._propertyValues[i]) : Class.forName(this._propertyValues[i], true, classLoader);
         classes.add(mClass);
      }

      Class[] array = new Class[classes.size()];
      classes.toArray(array);
      return array;
   }
}
