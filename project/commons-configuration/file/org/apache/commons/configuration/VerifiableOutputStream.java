package org.apache.commons.configuration;

import java.io.IOException;
import java.io.OutputStream;

abstract class VerifiableOutputStream extends OutputStream {
   public abstract void verify() throws IOException;
}
