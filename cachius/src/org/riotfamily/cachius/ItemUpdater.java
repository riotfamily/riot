package org.riotfamily.cachius;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.support.IOUtils;
import org.riotfamily.cachius.support.SessionUtils;
import org.riotfamily.cachius.support.TokenFilterWriter;
import org.springframework.util.Assert;

/**
 * Class that updates a CacheItem after request processing has finished.
 */
public class ItemUpdater {

	private static final String FILE_ENCODING = "UTF-8";

	private Log log = LogFactory.getLog(ItemUpdater.class);
	
	private CacheItem cacheItem;
	
	private HttpServletRequest request;
	
	private File tempFile;
	
	private boolean discard = false;
	
	private Writer writer;
	
	private OutputStream outputStream;
	
	public ItemUpdater(CacheItem cacheItem, HttpServletRequest request) {
		this.cacheItem = cacheItem;
		this.request = request;
	}

	protected File getTempFile() throws IOException {
		if (tempFile == null) {
			tempFile = cacheItem.createTempFile();
		}
		Assert.notNull(tempFile);
		return tempFile;
	}
	
	public Writer getWriter() throws UnsupportedEncodingException, 
			FileNotFoundException {
		
		if (writer == null) {
			if (outputStream != null) {
				throw new IllegalStateException("getWriter() must not be "
						+ "called after getOutputStream()!");
			}
			try {
				writer = new OutputStreamWriter(
						new FileOutputStream(getTempFile()), FILE_ENCODING);
		        
		        if (cacheItem.isFilterSessionId()) {
		            writer = new TokenFilterWriter(request.getSession().getId(), 
		                    "${jsessionid}", writer);
		        }
			}
			catch (Exception e) {
				discard();
				log.error(e);
			}
		}
		return writer;
	}
	
	public OutputStream getOutputStream() throws FileNotFoundException {
		if (outputStream == null) {
			if (writer != null) {
				throw new IllegalStateException("getOutputStream() must not be " 
						+ "called after getWriter()!");
			}
			try {
				outputStream = new FileOutputStream(getTempFile());
			}
			catch (Exception e) {
				discard();
				log.error(e);
			}
		}
		return outputStream;
	}
	
	public void discard() {
		discard = true;
	}
	
	public void setContentType(String contentType) {
		cacheItem.setContentType(contentType);
	}
	
	public void updateCacheItem() {
		IOUtils.closeStream(outputStream);
		IOUtils.closeWriter(writer);
		if (!discard) {
			if (tempFile == null) {
				log.debug("No content for item " + cacheItem.getKey());
				return;
			}
			if (writer != null) {
				if (SessionUtils.sessionStateChanged(request)) {
	            	log.debug("Session state has changed during " +
	            			"processing. Since possibly not all URLs " +
	            			"are encoded the response is NOT cached.");
	            	
	            	return;
	            }
			}
			cacheItem.update(tempFile, outputStream != null);
		}
		else {
			IOUtils.delete(tempFile);
			tempFile = null;
		}
	}
	
	
	public void finalize() {
		IOUtils.delete(tempFile);
	}
}
