package org.apache.commons.configuration.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventSource {
   private Collection listeners;
   private Collection errorListeners;
   private final Object lockDetailEventsCount = new Object();
   private int detailEvents;

   public EventSource() {
      this.initListeners();
   }

   public void addConfigurationListener(ConfigurationListener l) {
      checkListener(l);
      this.listeners.add(l);
   }

   public boolean removeConfigurationListener(ConfigurationListener l) {
      return this.listeners.remove(l);
   }

   public Collection getConfigurationListeners() {
      return Collections.unmodifiableCollection(new ArrayList(this.listeners));
   }

   public void clearConfigurationListeners() {
      this.listeners.clear();
   }

   public boolean isDetailEvents() {
      return this.checkDetailEvents(0);
   }

   public void setDetailEvents(boolean enable) {
      synchronized(this.lockDetailEventsCount) {
         if (enable) {
            ++this.detailEvents;
         } else {
            --this.detailEvents;
         }

      }
   }

   public void addErrorListener(ConfigurationErrorListener l) {
      checkListener(l);
      this.errorListeners.add(l);
   }

   public boolean removeErrorListener(ConfigurationErrorListener l) {
      return this.errorListeners.remove(l);
   }

   public void clearErrorListeners() {
      this.errorListeners.clear();
   }

   public Collection getErrorListeners() {
      return Collections.unmodifiableCollection(new ArrayList(this.errorListeners));
   }

   protected void fireEvent(int type, String propName, Object propValue, boolean before) {
      if (this.checkDetailEvents(-1)) {
         Iterator it = this.listeners.iterator();
         if (it.hasNext()) {
            ConfigurationEvent event = this.createEvent(type, propName, propValue, before);

            while(it.hasNext()) {
               ((ConfigurationListener)it.next()).configurationChanged(event);
            }
         }
      }

   }

   protected ConfigurationEvent createEvent(int type, String propName, Object propValue, boolean before) {
      return new ConfigurationEvent(this, type, propName, propValue, before);
   }

   protected void fireError(int type, String propName, Object propValue, Throwable ex) {
      Iterator it = this.errorListeners.iterator();
      if (it.hasNext()) {
         ConfigurationErrorEvent event = this.createErrorEvent(type, propName, propValue, ex);

         while(it.hasNext()) {
            ((ConfigurationErrorListener)it.next()).configurationError(event);
         }
      }

   }

   protected ConfigurationErrorEvent createErrorEvent(int type, String propName, Object propValue, Throwable ex) {
      return new ConfigurationErrorEvent(this, type, propName, propValue, ex);
   }

   protected Object clone() throws CloneNotSupportedException {
      EventSource copy = (EventSource)super.clone();
      copy.initListeners();
      return copy;
   }

   private static void checkListener(Object l) {
      if (l == null) {
         throw new IllegalArgumentException("Listener must not be null!");
      }
   }

   private void initListeners() {
      this.listeners = new CopyOnWriteArrayList();
      this.errorListeners = new CopyOnWriteArrayList();
   }

   private boolean checkDetailEvents(int limit) {
      synchronized(this.lockDetailEventsCount) {
         return this.detailEvents > limit;
      }
   }
}
