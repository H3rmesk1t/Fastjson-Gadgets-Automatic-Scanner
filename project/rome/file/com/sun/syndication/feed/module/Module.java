package com.sun.syndication.feed.module;

import com.sun.syndication.feed.CopyFrom;
import java.io.Serializable;

public interface Module extends Cloneable, CopyFrom, Serializable {
   String getUri();

   Object clone() throws CloneNotSupportedException;
}
