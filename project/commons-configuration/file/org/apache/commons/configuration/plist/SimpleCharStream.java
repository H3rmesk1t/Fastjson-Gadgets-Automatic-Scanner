package org.apache.commons.configuration.plist;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class SimpleCharStream {
   public static final boolean staticFlag = false;
   int bufsize;
   int available;
   int tokenBegin;
   public int bufpos;
   protected int[] bufline;
   protected int[] bufcolumn;
   protected int column;
   protected int line;
   protected boolean prevCharIsCR;
   protected boolean prevCharIsLF;
   protected Reader inputStream;
   protected char[] buffer;
   protected int maxNextCharInd;
   protected int inBuf;
   protected int tabSize;

   protected void setTabSize(int i) {
      this.tabSize = i;
   }

   protected int getTabSize(int i) {
      return this.tabSize;
   }

   protected void ExpandBuff(boolean wrapAround) {
      char[] newbuffer = new char[this.bufsize + 2048];
      int[] newbufline = new int[this.bufsize + 2048];
      int[] newbufcolumn = new int[this.bufsize + 2048];

      try {
         if (wrapAround) {
            System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
            System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
            this.buffer = newbuffer;
            System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
            System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
            this.bufline = newbufline;
            System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
            System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
            this.bufcolumn = newbufcolumn;
            this.maxNextCharInd = this.bufpos += this.bufsize - this.tokenBegin;
         } else {
            System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
            this.buffer = newbuffer;
            System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
            this.bufline = newbufline;
            System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
            this.bufcolumn = newbufcolumn;
            this.maxNextCharInd = this.bufpos -= this.tokenBegin;
         }
      } catch (Throwable var6) {
         throw new Error(var6.getMessage());
      }

      this.bufsize += 2048;
      this.available = this.bufsize;
      this.tokenBegin = 0;
   }

   protected void FillBuff() throws IOException {
      if (this.maxNextCharInd == this.available) {
         if (this.available == this.bufsize) {
            if (this.tokenBegin > 2048) {
               this.bufpos = this.maxNextCharInd = 0;
               this.available = this.tokenBegin;
            } else if (this.tokenBegin < 0) {
               this.bufpos = this.maxNextCharInd = 0;
            } else {
               this.ExpandBuff(false);
            }
         } else if (this.available > this.tokenBegin) {
            this.available = this.bufsize;
         } else if (this.tokenBegin - this.available < 2048) {
            this.ExpandBuff(true);
         } else {
            this.available = this.tokenBegin;
         }
      }

      try {
         int i;
         if ((i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd)) == -1) {
            this.inputStream.close();
            throw new IOException();
         } else {
            this.maxNextCharInd += i;
         }
      } catch (IOException var3) {
         --this.bufpos;
         this.backup(0);
         if (this.tokenBegin == -1) {
            this.tokenBegin = this.bufpos;
         }

         throw var3;
      }
   }

   public char BeginToken() throws IOException {
      this.tokenBegin = -1;
      char c = this.readChar();
      this.tokenBegin = this.bufpos;
      return c;
   }

   protected void UpdateLineColumn(char c) {
      ++this.column;
      if (this.prevCharIsLF) {
         this.prevCharIsLF = false;
         this.line += this.column = 1;
      } else if (this.prevCharIsCR) {
         this.prevCharIsCR = false;
         if (c == '\n') {
            this.prevCharIsLF = true;
         } else {
            this.line += this.column = 1;
         }
      }

      switch(c) {
      case '\t':
         --this.column;
         this.column += this.tabSize - this.column % this.tabSize;
         break;
      case '\n':
         this.prevCharIsLF = true;
      case '\u000b':
      case '\f':
      default:
         break;
      case '\r':
         this.prevCharIsCR = true;
      }

      this.bufline[this.bufpos] = this.line;
      this.bufcolumn[this.bufpos] = this.column;
   }

   public char readChar() throws IOException {
      if (this.inBuf > 0) {
         --this.inBuf;
         if (++this.bufpos == this.bufsize) {
            this.bufpos = 0;
         }

         return this.buffer[this.bufpos];
      } else {
         if (++this.bufpos >= this.maxNextCharInd) {
            this.FillBuff();
         }

         char c = this.buffer[this.bufpos];
         this.UpdateLineColumn(c);
         return c;
      }
   }

   /** @deprecated */
   @Deprecated
   public int getColumn() {
      return this.bufcolumn[this.bufpos];
   }

   /** @deprecated */
   @Deprecated
   public int getLine() {
      return this.bufline[this.bufpos];
   }

   public int getEndColumn() {
      return this.bufcolumn[this.bufpos];
   }

   public int getEndLine() {
      return this.bufline[this.bufpos];
   }

   public int getBeginColumn() {
      return this.bufcolumn[this.tokenBegin];
   }

   public int getBeginLine() {
      return this.bufline[this.tokenBegin];
   }

   public void backup(int amount) {
      this.inBuf += amount;
      if ((this.bufpos -= amount) < 0) {
         this.bufpos += this.bufsize;
      }

   }

   public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
      this.bufpos = -1;
      this.column = 0;
      this.line = 1;
      this.prevCharIsCR = false;
      this.prevCharIsLF = false;
      this.maxNextCharInd = 0;
      this.inBuf = 0;
      this.tabSize = 8;
      this.inputStream = dstream;
      this.line = startline;
      this.column = startcolumn - 1;
      this.available = this.bufsize = buffersize;
      this.buffer = new char[buffersize];
      this.bufline = new int[buffersize];
      this.bufcolumn = new int[buffersize];
   }

   public SimpleCharStream(Reader dstream, int startline, int startcolumn) {
      this((Reader)dstream, startline, startcolumn, 4096);
   }

   public SimpleCharStream(Reader dstream) {
      this((Reader)dstream, 1, 1, 4096);
   }

   public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize) {
      this.inputStream = dstream;
      this.line = startline;
      this.column = startcolumn - 1;
      if (this.buffer == null || buffersize != this.buffer.length) {
         this.available = this.bufsize = buffersize;
         this.buffer = new char[buffersize];
         this.bufline = new int[buffersize];
         this.bufcolumn = new int[buffersize];
      }

      this.prevCharIsLF = this.prevCharIsCR = false;
      this.tokenBegin = this.inBuf = this.maxNextCharInd = 0;
      this.bufpos = -1;
   }

   public void ReInit(Reader dstream, int startline, int startcolumn) {
      this.ReInit((Reader)dstream, startline, startcolumn, 4096);
   }

   public void ReInit(Reader dstream) {
      this.ReInit((Reader)dstream, 1, 1, 4096);
   }

   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
      this((Reader)(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding)), startline, startcolumn, buffersize);
   }

   public SimpleCharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
      this((Reader)(new InputStreamReader(dstream)), startline, startcolumn, buffersize);
   }

   public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
      this(dstream, encoding, startline, startcolumn, 4096);
   }

   public SimpleCharStream(InputStream dstream, int startline, int startcolumn) {
      this((InputStream)dstream, startline, startcolumn, 4096);
   }

   public SimpleCharStream(InputStream dstream, String encoding) throws UnsupportedEncodingException {
      this(dstream, encoding, 1, 1, 4096);
   }

   public SimpleCharStream(InputStream dstream) {
      this((InputStream)dstream, 1, 1, 4096);
   }

   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
      this.ReInit((Reader)(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding)), startline, startcolumn, buffersize);
   }

   public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize) {
      this.ReInit((Reader)(new InputStreamReader(dstream)), startline, startcolumn, buffersize);
   }

   public void ReInit(InputStream dstream, String encoding) throws UnsupportedEncodingException {
      this.ReInit(dstream, encoding, 1, 1, 4096);
   }

   public void ReInit(InputStream dstream) {
      this.ReInit((InputStream)dstream, 1, 1, 4096);
   }

   public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
      this.ReInit(dstream, encoding, startline, startcolumn, 4096);
   }

   public void ReInit(InputStream dstream, int startline, int startcolumn) {
      this.ReInit((InputStream)dstream, startline, startcolumn, 4096);
   }

   public String GetImage() {
      return this.bufpos >= this.tokenBegin ? new String(this.buffer, this.tokenBegin, this.bufpos - this.tokenBegin + 1) : new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
   }

   public char[] GetSuffix(int len) {
      char[] ret = new char[len];
      if (this.bufpos + 1 >= len) {
         System.arraycopy(this.buffer, this.bufpos - len + 1, ret, 0, len);
      } else {
         System.arraycopy(this.buffer, this.bufsize - (len - this.bufpos - 1), ret, 0, len - this.bufpos - 1);
         System.arraycopy(this.buffer, 0, ret, len - this.bufpos - 1, this.bufpos + 1);
      }

      return ret;
   }

   public void Done() {
      this.buffer = null;
      this.bufline = null;
      this.bufcolumn = null;
   }

   public void adjustBeginLineColumn(int newLine, int newCol) {
      int start = this.tokenBegin;
      int len;
      if (this.bufpos >= this.tokenBegin) {
         len = this.bufpos - this.tokenBegin + this.inBuf + 1;
      } else {
         len = this.bufsize - this.tokenBegin + this.bufpos + 1 + this.inBuf;
      }

      int i = 0;
      int j = 0;
      int k = false;
      int nextColDiff = false;

      int columnDiff;
      int var10000;
      for(columnDiff = 0; i < len; ++i) {
         var10000 = this.bufline[j = start % this.bufsize];
         ++start;
         int k;
         if (var10000 != this.bufline[k = start % this.bufsize]) {
            break;
         }

         this.bufline[j] = newLine;
         int nextColDiff = columnDiff + this.bufcolumn[k] - this.bufcolumn[j];
         this.bufcolumn[j] = newCol + columnDiff;
         columnDiff = nextColDiff;
      }

      if (i < len) {
         this.bufline[j] = newLine++;
         this.bufcolumn[j] = newCol + columnDiff;

         while(i++ < len) {
            var10000 = this.bufline[j = start % this.bufsize];
            ++start;
            if (var10000 != this.bufline[start % this.bufsize]) {
               this.bufline[j] = newLine++;
            } else {
               this.bufline[j] = newLine;
            }
         }
      }

      this.line = this.bufline[j];
      this.column = this.bufcolumn[j];
   }
}
