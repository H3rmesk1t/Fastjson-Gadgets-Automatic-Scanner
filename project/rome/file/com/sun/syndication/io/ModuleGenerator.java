package com.sun.syndication.io;

import com.sun.syndication.feed.module.Module;
import java.util.Set;
import org.jdom.Element;

public interface ModuleGenerator {
   String getNamespaceUri();

   Set getNamespaces();

   void generate(Module var1, Element var2);
}
