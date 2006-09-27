package org.riotfamily.common.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ViewContext {

	private static ThreadLocal threadLocal = new ThreadLocal();
	
	private ViewContext() {
	}
		
	public static void execute(HttpServletRequest request, 
			HttpServletResponse response, Callback callback) 
			throws Exception {
		
		RequestAndResponse rar = (RequestAndResponse) threadLocal.get();
		if (rar == null) {
			threadLocal.set(new RequestAndResponse(request, response)); 
		}
		try {
			callback.doInContext();
		}
		finally {
			if (rar == null) {
				threadLocal.set(null);	
			}
		}
	}
	
	private static RequestAndResponse getRequestAndResponse() {
		RequestAndResponse rar = (RequestAndResponse) threadLocal.get();
		return rar;
	}
	
	public static HttpServletRequest getRequest() {
		return getRequestAndResponse().getRequest();
	}
	
	public static HttpServletResponse getResponse() {
		return getRequestAndResponse().getResponse();
	}
	
	public interface Callback {
	
		public void doInContext() throws Exception;
		
	}
	
	private static class RequestAndResponse {
		
		private HttpServletRequest request;
		
		private HttpServletResponse response;
		
		private RequestAndResponse(HttpServletRequest request, 
				HttpServletResponse response) {

			this.request = request;
			this.response = response;
		}

		public HttpServletRequest getRequest() {
			return this.request;
		}

		public HttpServletResponse getResponse() {
			return this.response;
		}
		
	}

}
