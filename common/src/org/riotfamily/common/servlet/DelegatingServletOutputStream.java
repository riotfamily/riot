package org.riotfamily.common.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * ServletOutputStream that delegates all methods to a regular 
 * {@link OutputStream}. 
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DelegatingServletOutputStream extends ServletOutputStream {

    private final OutputStream targetStream;

    /**
     * Create a new DelegatingServletOutputStream.
     * @param targetStream the target OutputStream
     */
    public DelegatingServletOutputStream(OutputStream targetStream) {
            this.targetStream = targetStream;
    }

    public void write(int b) throws IOException {
            this.targetStream.write(b);
    }

    public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
    }

    public void close() throws IOException {
            super.close();
            this.targetStream.close();
    }

}
