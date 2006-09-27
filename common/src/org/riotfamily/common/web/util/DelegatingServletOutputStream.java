package org.riotfamily.common.web.util;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

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
