package org.apache.commons.configuration.plist;

public interface PropertyListParserConstants {
   int EOF = 0;
   int SINGLE_LINE_COMMENT = 9;
   int ARRAY_BEGIN = 11;
   int ARRAY_END = 12;
   int ARRAY_SEPARATOR = 13;
   int DICT_BEGIN = 14;
   int DICT_END = 15;
   int DICT_SEPARATOR = 16;
   int EQUAL = 17;
   int DATA_START = 18;
   int DATA_END = 19;
   int DATE_START = 20;
   int QUOTE = 21;
   int LETTER = 22;
   int WHITE = 23;
   int HEXA = 24;
   int DATA = 25;
   int DATE = 26;
   int STRING = 27;
   int QUOTED_STRING = 28;
   int ESCAPED_QUOTE = 29;
   int DEFAULT = 0;
   int IN_COMMENT = 1;
   int IN_SINGLE_LINE_COMMENT = 2;
   String[] tokenImage = new String[]{"<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "\"/*\"", "<token of kind 6>", "\"*/\"", "\"//\"", "<SINGLE_LINE_COMMENT>", "<token of kind 10>", "\"(\"", "\")\"", "\",\"", "\"{\"", "\"}\"", "\";\"", "\"=\"", "\"<\"", "\">\"", "\"<*D\"", "\"\\\"\"", "<LETTER>", "<WHITE>", "<HEXA>", "<DATA>", "<DATE>", "<STRING>", "<QUOTED_STRING>", "\"\\\\\\\"\""};
}
