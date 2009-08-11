package org.riotfamily.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.FileCopyUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class StreamCopyThread extends Thread {

	private InputStream in;

	private OutputStream out;

	public StreamCopyThread(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
    }

    public void run() {
    	try {
        	FileCopyUtils.copy(in, out);
    	}
    	catch (IOException e) {
    	}
    }
}
