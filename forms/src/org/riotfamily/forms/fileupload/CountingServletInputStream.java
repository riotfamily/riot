package org.riotfamily.forms.fileupload;

import java.io.IOException;

import javax.servlet.ServletInputStream;

/**
 * ServletInputStream that counts the number of bytes read. This class is used
 * to provide progress information for file uploads.
 */
public class CountingServletInputStream extends ServletInputStream {

	private ServletInputStream sourceStream;

	private long bytesRead;
	
	public CountingServletInputStream() {
	}

	public void setSourceStream(ServletInputStream sourceStream) {
		this.sourceStream = sourceStream;
	}
	
	public int read() throws IOException {
		if (sourceStream == null) {
			throw new IllegalMonitorStateException("No sourceStream set");
		}
		int i = sourceStream.read();
		if (i >= 0) {
			bytesRead ++;
		}
		return i;
	}

	public long getBytesRead() {
		return bytesRead;
	}
}
