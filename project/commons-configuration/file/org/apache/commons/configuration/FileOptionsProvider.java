package org.apache.commons.configuration;

import java.util.Map;

public interface FileOptionsProvider {
   String CURRENT_USER = "currentUser";
   String VERSIONING = "versioning";
   String PROXY_HOST = "proxyHost";
   String PROXY_PORT = "proxyPort";
   String MAX_HOST_CONNECTIONS = "maxHostConnections";
   String MAX_TOTAL_CONNECTIONS = "maxTotalConnections";

   Map getOptions();
}
