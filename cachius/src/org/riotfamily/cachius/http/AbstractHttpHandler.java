package org.riotfamily.cachius.http;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheHandler;
import org.riotfamily.cachius.http.content.Directives;
import org.riotfamily.cachius.http.support.SessionIdEncoder;
import org.riotfamily.cachius.persistence.DiskStore;

public abstract class AbstractHttpHandler implements CacheHandler {

	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private Directives directives;

	private int gzipThreshold = 200;
	
	public AbstractHttpHandler(HttpServletRequest request,
			HttpServletResponse response) {
	
		this(request, response, Directives.DEFAULTS);
	}
	
	public AbstractHttpHandler(HttpServletRequest request,
			HttpServletResponse response, Directives directives) {
	
		this.request = request;
		this.response = response;
		this.directives = directives;
	}

	public void setGzipThreshold(int gzipThreshold) {
		this.gzipThreshold = gzipThreshold;
	}
	
	protected HttpServletRequest getRequest() {
		return request;
	}
	
	protected HttpServletResponse getResponse() {
		return response;
	}
	
	public String getCacheKey() {
		return request.getRequestURL().toString();
	}

	public String getCacheRegion() {
		return null;
	}

	public long getLastModified() {
		return System.currentTimeMillis();
	}

	public void handleUncached() throws Exception {
		handleRequest(request, response);
	}

	public void serve(Serializable obj) throws Exception {
		assert obj instanceof ResponseData;
		ResponseData data = (ResponseData) obj;
		data.serve(request, response);
	}

	public Serializable capture(DiskStore diskStore) throws Exception {
		ResponseData data = new ResponseData(response.getCharacterEncoding());
		SessionIdEncoder sessionIdEncoder = new SessionIdEncoder(request);
		CachiusResponse cachiusResponse = new CachiusResponse(data, diskStore, 
				sessionIdEncoder, isCompressible(), gzipThreshold , directives);
		
		handleRequest(request, cachiusResponse);
		cachiusResponse.stopCapturing();
		return data;
	}
	
	protected boolean isCompressible() {
		return false;
	}
	
	protected abstract void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception; 

}
