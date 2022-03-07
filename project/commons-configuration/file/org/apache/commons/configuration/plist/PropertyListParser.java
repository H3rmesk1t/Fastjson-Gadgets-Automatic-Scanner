package org.apache.commons.configuration.plist;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration.HierarchicalConfiguration;

class PropertyListParser implements PropertyListParserConstants {
   public PropertyListParserTokenManager token_source;
   SimpleCharStream jj_input_stream;
   public Token token;
   public Token jj_nt;
   private int jj_ntk;
   private Token jj_scanpos;
   private Token jj_lastpos;
   private int jj_la;
   private int jj_gen;
   private final int[] jj_la1;
   private static int[] jj_la1_0;
   private final PropertyListParser.JJCalls[] jj_2_rtns;
   private boolean jj_rescan;
   private int jj_gc;
   private final PropertyListParser.LookaheadSuccess jj_ls;
   private List jj_expentries;
   private int[] jj_expentry;
   private int jj_kind;
   private int[] jj_lasttokens;
   private int jj_endpos;

   protected String removeQuotes(String s) {
      if (s == null) {
         return null;
      } else {
         if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
         }

         return s;
      }
   }

   protected String unescapeQuotes(String s) {
      return s.replaceAll("\\\\\"", "\"");
   }

   protected byte[] filterData(String s) throws ParseException {
      if (s == null) {
         return null;
      } else {
         if (s.startsWith("<") && s.endsWith(">") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
         }

         s = s.replaceAll("\\s", "");
         if (s.length() % 2 != 0) {
            s = "0" + s;
         }

         try {
            return Hex.decodeHex(s.toCharArray());
         } catch (Exception var3) {
            throw new ParseException("Unable to parse the byte[] : " + var3.getMessage());
         }
      }
   }

   protected Date parseDate(String s) throws ParseException {
      return PropertyListConfiguration.parseDate(s);
   }

   public final PropertyListConfiguration parse() throws ParseException {
      PropertyListConfiguration configuration = null;
      configuration = this.Dictionary();
      this.jj_consume_token(0);
      return configuration;
   }

   public final PropertyListConfiguration Dictionary() throws ParseException {
      PropertyListConfiguration configuration = new PropertyListConfiguration();
      List children = new ArrayList();
      HierarchicalConfiguration.Node child = null;
      this.jj_consume_token(14);

      while(true) {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 27:
         case 28:
            child = this.Property();
            if (child.getValue() instanceof HierarchicalConfiguration) {
               HierarchicalConfiguration conf = (HierarchicalConfiguration)child.getValue();
               HierarchicalConfiguration.Node root = conf.getRoot();
               root.setName(child.getName());
               children.add(root);
            } else {
               children.add(child);
            }
            break;
         default:
            this.jj_la1[0] = this.jj_gen;
            this.jj_consume_token(15);

            for(int i = 0; i < children.size(); ++i) {
               child = (HierarchicalConfiguration.Node)children.get(i);
               configuration.getRoot().addChild(child);
            }

            return configuration;
         }
      }
   }

   public final HierarchicalConfiguration.Node Property() throws ParseException {
      String key = null;
      Object value = null;
      HierarchicalConfiguration.Node node = new HierarchicalConfiguration.Node();
      key = this.String();
      node.setName(key);
      this.jj_consume_token(17);
      value = this.Element();
      node.setValue(value);
      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 16:
         this.jj_consume_token(16);
         break;
      default:
         this.jj_la1[1] = this.jj_gen;
      }

      return node;
   }

   public final Object Element() throws ParseException {
      Object value = null;
      if (this.jj_2_1(2)) {
         Object value = this.Array();
         return value;
      } else {
         switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
         case 14:
            Object value = this.Dictionary();
            return value;
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         default:
            this.jj_la1[2] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
         case 25:
            Object value = this.Data();
            return value;
         case 26:
            Object value = this.Date();
            return value;
         case 27:
         case 28:
            value = this.String();
            return value;
         }
      }
   }

   public final List Array() throws ParseException {
      ArrayList list;
      list = new ArrayList();
      Object element = null;
      this.jj_consume_token(11);
      label28:
      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 11:
      case 14:
      case 25:
      case 26:
      case 27:
      case 28:
         element = this.Element();
         list.add(element);

         while(true) {
            switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13:
               this.jj_consume_token(13);
               element = this.Element();
               list.add(element);
               break;
            default:
               this.jj_la1[3] = this.jj_gen;
               break label28;
            }
         }
      case 12:
      case 13:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      default:
         this.jj_la1[4] = this.jj_gen;
      }

      this.jj_consume_token(12);
      return list;
   }

   public final String String() throws ParseException {
      Token token = null;
      String value = null;
      switch(this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
      case 27:
         token = this.jj_consume_token(27);
         return token.image;
      case 28:
         token = this.jj_consume_token(28);
         return this.unescapeQuotes(this.removeQuotes(token.image));
      default:
         this.jj_la1[5] = this.jj_gen;
         this.jj_consume_token(-1);
         throw new ParseException();
      }
   }

   public final byte[] Data() throws ParseException {
      Token token = this.jj_consume_token(25);
      return this.filterData(token.image);
   }

   public final Date Date() throws ParseException {
      Token token = this.jj_consume_token(26);
      return this.parseDate(token.image);
   }

   private boolean jj_2_1(int xla) {
      this.jj_la = xla;
      this.jj_lastpos = this.jj_scanpos = this.token;

      boolean var3;
      try {
         boolean var2 = !this.jj_3_1();
         return var2;
      } catch (PropertyListParser.LookaheadSuccess var7) {
         var3 = true;
      } finally {
         this.jj_save(0, xla);
      }

      return var3;
   }

   private boolean jj_3_1() {
      return this.jj_3R_3();
   }

   private boolean jj_3R_5() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3_1()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_6()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_7()) {
               this.jj_scanpos = xsp;
               if (this.jj_3R_8()) {
                  this.jj_scanpos = xsp;
                  if (this.jj_3R_9()) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean jj_3R_14() {
      return this.jj_scan_token(28);
   }

   private boolean jj_3R_11() {
      Token xsp = this.jj_scanpos;
      if (this.jj_3R_14()) {
         this.jj_scanpos = xsp;
         if (this.jj_3R_15()) {
            return true;
         }
      }

      return false;
   }

   private boolean jj_3R_13() {
      return this.jj_scan_token(26);
   }

   private boolean jj_3R_10() {
      return this.jj_scan_token(14);
   }

   private boolean jj_3R_9() {
      return this.jj_3R_13();
   }

   private boolean jj_3R_8() {
      return this.jj_3R_12();
   }

   private boolean jj_3R_12() {
      return this.jj_scan_token(25);
   }

   private boolean jj_3R_7() {
      return this.jj_3R_11();
   }

   private boolean jj_3R_4() {
      return this.jj_3R_5();
   }

   private boolean jj_3R_6() {
      return this.jj_3R_10();
   }

   private boolean jj_3R_15() {
      return this.jj_scan_token(27);
   }

   private boolean jj_3R_3() {
      if (this.jj_scan_token(11)) {
         return true;
      } else {
         Token xsp = this.jj_scanpos;
         if (this.jj_3R_4()) {
            this.jj_scanpos = xsp;
         }

         return this.jj_scan_token(12);
      }
   }

   private static void jj_la1_init_0() {
      jj_la1_0 = new int[]{402653184, 65536, 503332864, 8192, 503334912, 402653184};
   }

   public PropertyListParser(InputStream stream) {
      this(stream, (String)null);
   }

   public PropertyListParser(InputStream stream, String encoding) {
      this.jj_la1 = new int[6];
      this.jj_2_rtns = new PropertyListParser.JJCalls[1];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new PropertyListParser.LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];

      try {
         this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source = new PropertyListParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 6; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new PropertyListParser.JJCalls();
      }

   }

   public void ReInit(InputStream stream) {
      this.ReInit(stream, (String)null);
   }

   public void ReInit(InputStream stream, String encoding) {
      try {
         this.jj_input_stream.ReInit(stream, encoding, 1, 1);
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }

      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 6; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new PropertyListParser.JJCalls();
      }

   }

   public PropertyListParser(Reader stream) {
      this.jj_la1 = new int[6];
      this.jj_2_rtns = new PropertyListParser.JJCalls[1];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new PropertyListParser.LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];
      this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
      this.token_source = new PropertyListParserTokenManager(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 6; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new PropertyListParser.JJCalls();
      }

   }

   public void ReInit(Reader stream) {
      this.jj_input_stream.ReInit((Reader)stream, 1, 1);
      this.token_source.ReInit(this.jj_input_stream);
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 6; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new PropertyListParser.JJCalls();
      }

   }

   public PropertyListParser(PropertyListParserTokenManager tm) {
      this.jj_la1 = new int[6];
      this.jj_2_rtns = new PropertyListParser.JJCalls[1];
      this.jj_rescan = false;
      this.jj_gc = 0;
      this.jj_ls = new PropertyListParser.LookaheadSuccess();
      this.jj_expentries = new ArrayList();
      this.jj_kind = -1;
      this.jj_lasttokens = new int[100];
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 6; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new PropertyListParser.JJCalls();
      }

   }

   public void ReInit(PropertyListParserTokenManager tm) {
      this.token_source = tm;
      this.token = new Token();
      this.jj_ntk = -1;
      this.jj_gen = 0;

      int i;
      for(i = 0; i < 6; ++i) {
         this.jj_la1[i] = -1;
      }

      for(i = 0; i < this.jj_2_rtns.length; ++i) {
         this.jj_2_rtns[i] = new PropertyListParser.JJCalls();
      }

   }

   private Token jj_consume_token(int kind) throws ParseException {
      Token oldToken;
      if ((oldToken = this.token).next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      if (this.token.kind != kind) {
         this.token = oldToken;
         this.jj_kind = kind;
         throw this.generateParseException();
      } else {
         ++this.jj_gen;
         if (++this.jj_gc > 100) {
            this.jj_gc = 0;

            for(int i = 0; i < this.jj_2_rtns.length; ++i) {
               for(PropertyListParser.JJCalls c = this.jj_2_rtns[i]; c != null; c = c.next) {
                  if (c.gen < this.jj_gen) {
                     c.first = null;
                  }
               }
            }
         }

         return this.token;
      }
   }

   private boolean jj_scan_token(int kind) {
      if (this.jj_scanpos == this.jj_lastpos) {
         --this.jj_la;
         if (this.jj_scanpos.next == null) {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
         } else {
            this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
         }
      } else {
         this.jj_scanpos = this.jj_scanpos.next;
      }

      if (this.jj_rescan) {
         int i = 0;

         Token tok;
         for(tok = this.token; tok != null && tok != this.jj_scanpos; tok = tok.next) {
            ++i;
         }

         if (tok != null) {
            this.jj_add_error_token(kind, i);
         }
      }

      if (this.jj_scanpos.kind != kind) {
         return true;
      } else if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
         throw this.jj_ls;
      } else {
         return false;
      }
   }

   public final Token getNextToken() {
      if (this.token.next != null) {
         this.token = this.token.next;
      } else {
         this.token = this.token.next = this.token_source.getNextToken();
      }

      this.jj_ntk = -1;
      ++this.jj_gen;
      return this.token;
   }

   public final Token getToken(int index) {
      Token t = this.token;

      for(int i = 0; i < index; ++i) {
         if (t.next != null) {
            t = t.next;
         } else {
            t = t.next = this.token_source.getNextToken();
         }
      }

      return t;
   }

   private int jj_ntk() {
      return (this.jj_nt = this.token.next) == null ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind);
   }

   private void jj_add_error_token(int kind, int pos) {
      if (pos < 100) {
         if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
         } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];

            for(int i = 0; i < this.jj_endpos; ++i) {
               this.jj_expentry[i] = this.jj_lasttokens[i];
            }

            Iterator it = this.jj_expentries.iterator();

            label41:
            while(true) {
               int[] oldentry;
               do {
                  if (!it.hasNext()) {
                     break label41;
                  }

                  oldentry = (int[])((int[])it.next());
               } while(oldentry.length != this.jj_expentry.length);

               for(int i = 0; i < this.jj_expentry.length; ++i) {
                  if (oldentry[i] != this.jj_expentry[i]) {
                     continue label41;
                  }
               }

               this.jj_expentries.add(this.jj_expentry);
               break;
            }

            if (pos != 0) {
               this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
            }
         }

      }
   }

   public ParseException generateParseException() {
      this.jj_expentries.clear();
      boolean[] la1tokens = new boolean[30];
      if (this.jj_kind >= 0) {
         la1tokens[this.jj_kind] = true;
         this.jj_kind = -1;
      }

      int i;
      int j;
      for(i = 0; i < 6; ++i) {
         if (this.jj_la1[i] == this.jj_gen) {
            for(j = 0; j < 32; ++j) {
               if ((jj_la1_0[i] & 1 << j) != 0) {
                  la1tokens[j] = true;
               }
            }
         }
      }

      for(i = 0; i < 30; ++i) {
         if (la1tokens[i]) {
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
         }
      }

      this.jj_endpos = 0;
      this.jj_rescan_token();
      this.jj_add_error_token(0, 0);
      int[][] exptokseq = new int[this.jj_expentries.size()][];

      for(j = 0; j < this.jj_expentries.size(); ++j) {
         exptokseq[j] = (int[])this.jj_expentries.get(j);
      }

      return new ParseException(this.token, exptokseq, tokenImage);
   }

   public final void enable_tracing() {
   }

   public final void disable_tracing() {
   }

   private void jj_rescan_token() {
      this.jj_rescan = true;

      for(int i = 0; i < 1; ++i) {
         try {
            PropertyListParser.JJCalls p = this.jj_2_rtns[i];

            do {
               if (p.gen > this.jj_gen) {
                  this.jj_la = p.arg;
                  this.jj_lastpos = this.jj_scanpos = p.first;
                  switch(i) {
                  case 0:
                     this.jj_3_1();
                  }
               }

               p = p.next;
            } while(p != null);
         } catch (PropertyListParser.LookaheadSuccess var3) {
         }
      }

      this.jj_rescan = false;
   }

   private void jj_save(int index, int xla) {
      PropertyListParser.JJCalls p;
      for(p = this.jj_2_rtns[index]; p.gen > this.jj_gen; p = p.next) {
         if (p.next == null) {
            p = p.next = new PropertyListParser.JJCalls();
            break;
         }
      }

      p.gen = this.jj_gen + xla - this.jj_la;
      p.first = this.token;
      p.arg = xla;
   }

   static {
      jj_la1_init_0();
   }

   static final class JJCalls {
      int gen;
      Token first;
      int arg;
      PropertyListParser.JJCalls next;
   }

   private static final class LookaheadSuccess extends Error {
      private LookaheadSuccess() {
      }

      // $FF: synthetic method
      LookaheadSuccess(Object x0) {
         this();
      }
   }
}
