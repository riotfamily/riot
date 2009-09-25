/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.linkcheck;

import java.io.IOException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.RiotLog;

public class HttpStatusChecker {	
	
	private RiotLog log = RiotLog.get(this);
	
	private HttpClient client = new HttpClient();
	
	public HttpStatusChecker() {
		HttpConnectionManagerParams connectionParams = client.getHttpConnectionManager().getParams();
		connectionParams.setConnectionTimeout((int) FormatUtils.parseMillis("2s"));
		connectionParams.setSoTimeout((int) FormatUtils.parseMillis("5s"));
		connectionParams.setStaleCheckingEnabled(true);
	}
	
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
				log.info(e.getMessage());
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
