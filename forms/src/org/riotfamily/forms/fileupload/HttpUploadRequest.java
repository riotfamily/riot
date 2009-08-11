package org.riotfamily.forms.fileupload;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * HttpServletRequestWrapper that returns a 
 * {@link org.riotfamily.forms.fileupload.CountingServletInputStream CountingInputStream}.
 */
public class HttpUploadRequest extends HttpServletRequestWrapper {
	
	private ServletInputStream wrappedStream;
	
	private CountingServletInputStream countingInputStream;
	
	public HttpUploadRequest(HttpServletRequest request) {
		super(request);
		this.countingInputStream = new CountingServletInputStream();
	}
		
	public CountingServletInputStream getCountingInputStream() {
		return countingInputStream;
	}
	
	public ServletInputStream getInputStream() throws IOException {
		if (wrappedStream == null) {
			wrappedStream = super.getInputStream();
			countingInputStream.setSourceStream(wrappedStream);
		}
		return countingInputStream;
	}
			
}
