package com.sun.syndication.io;

import com.sun.syndication.feed.module.Module;
import org.jdom.Element;

public interface ModuleParser {
   String getNamespaceUri();

   Module parse(Element var1);
}
