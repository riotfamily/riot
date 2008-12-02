package org.riotfamily.linkcheck;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.FormatUtils;

public class HttpStatusChecker {	
	
	private RiotLog log = RiotLog.get(this);
	
	private HttpClient client = new HttpClient();
	
	private HttpMethod createMethod(String url) {
		try {
			return new GetWithoutBodyMethod(url);
		}
		catch (IllegalArgumentException e) {
			return new GetWithoutBodyMethod(FormatUtils.uriEscape(url));
		}
	}
	
	public boolean isOkay(Link link) {
		try {
			HttpMethod method = createMethod(link.getDestination());
			HttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler();
			HttpMethodParams params = new HttpMethodParams();
			params.setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
			method.setParams(params);
			method.setFollowRedirects(true);
	
			try {
				int statusCode = client.executeMethod(method);
				if (log.isDebugEnabled()) {
					if (log.isDebugEnabled()) {
						log.debug("Check: " + link + " [" + statusCode + "]");
					}
				}
				link.setStatusCode(statusCode);
				if (statusCode == HttpStatus.SC_OK) {
					return true;
				}
				else {		
					link.setStatusText(method.getStatusText());
				}						
			}
			catch (IOException e) {
				log.error(e);
			}
			finally {
				method.releaseConnection();
			}
		}
		catch (IllegalArgumentException e) {
		}
		catch (IllegalStateException e) {
		}
		return false;
	}
	
	private static class GetWithoutBodyMethod extends HeadMethod {
		public GetWithoutBodyMethod(String uri) {
			super(uri);
		}
		public String getName() {
			return "GET";
		}
	}
	
}
