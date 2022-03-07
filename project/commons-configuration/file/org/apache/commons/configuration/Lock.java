package org.apache.commons.configuration;

public class Lock {
   private static String counterLock = "Lock";
   private static int counter;
   private final String name;
   private final int instanceId;

   public Lock(String name) {
      this.name = name;
      synchronized(counterLock) {
         this.instanceId = ++counter;
      }
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return "Lock: " + this.name + " id = " + this.instanceId + ": " + super.toString();
   }
}
