package org.apache.commons.configuration.resolver;

import java.net.URL;
import java.util.Map;

public interface EntityRegistry {
   void registerEntityId(String var1, URL var2);

   Map getRegisteredEntities();
}
