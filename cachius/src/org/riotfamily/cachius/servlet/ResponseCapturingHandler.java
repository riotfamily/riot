package org.riotfamily.cachius.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheItem;

/**
 * Abstract CacheHandler that creates a response wrapper and captures
 * everything that is written to the response's OutputStream or Writer.
 */
public abstract class ResponseCapturingHandler extends SessionIdCacheHandler {

	private HttpServletResponse response;
	
	public ResponseCapturingHandler(HttpServletRequest request,
			HttpServletResponse response, 
			CacheKeyAugmentor cacheKeyAugmentor) {
		
		super(request, cacheKeyAugmentor);
		this.response = response;
	}

	protected boolean updateCacheItemInternal(CacheItem cacheItem) throws Exception {
		CachiusResponse cachiusResponse = new CachiusResponse(
				cacheItem, getSessionIdEncoder(), 
				response.getCharacterEncoding());
		
		handleInternal(cachiusResponse);
		
		cachiusResponse.stopCapturing();
		cachiusResponse.updateHeaders();
		return cachiusResponse.getStatus() < 500;
	}
	
	public void handleUncached() throws Exception {
		handleInternal(response);
	}
	
	protected abstract void handleInternal(HttpServletResponse response) 
			throws Exception;
	
	public final void writeCacheItem(CacheItem cacheItem) throws IOException {
		HttpServletResponse wrapper = response;
		if (getSessionIdEncoder().urlsNeedEncoding()) {
			wrapper = new SessionIdInsertingResponseWrapper(response, getSessionIdEncoder());
		}
		cacheItem.writeTo(wrapper);
	}
}
