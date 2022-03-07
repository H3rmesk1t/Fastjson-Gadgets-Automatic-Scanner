package org.apache.commons.configuration;

public interface FileSystemBased {
   void setFileSystem(FileSystem var1);

   void resetFileSystem();

   FileSystem getFileSystem();
}
