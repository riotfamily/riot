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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.riotfamily.common.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpStatusChecker {	
	
	private Logger log = LoggerFactory.getLogger(HttpStatusChecker.class);
	
	private CloseableHttpClient client;
	
	public HttpStatusChecker() {
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout((int) FormatUtils.parseMillis("2s"))
				.setSocketTimeout((int) FormatUtils.parseMillis("5s"))
				.setStaleConnectionCheckEnabled(true)
				.build();
		
		client = HttpClients.custom()
					.setDefaultRequestConfig(config)
					.build();
	}
	
	private HttpRequestBase createMethod(String url) {
		try {
			return new HttpGetWithoutBody(url);
		}
		catch (IllegalArgumentException e) {
			return new HttpGetWithoutBody(FormatUtils.uriEscape(url));
		}
	}
	
	public boolean isOkay(BrokenLink link) {
		try {
			HttpRequestBase http = createMethod(link.getDestination());
	
			try {
				HttpResponse httpResponse = client.execute(http); 
				int statusCode = httpResponse.getStatusLine().getStatusCode();
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
					link.setStatusText(httpResponse.getStatusLine().getReasonPhrase());
				}						
			}
			catch (IOException e) {
				log.info(e.getMessage());
			}
			finally {
				http.releaseConnection();
			}
		}
		catch (IllegalArgumentException e) {
		}
		catch (IllegalStateException e) {
		}
		return false;
	}
	
	private static class HttpGetWithoutBody extends HttpHead {
		
		public HttpGetWithoutBody(String uri) {
			super(uri);
		}
		
		@Override
		public String getMethod() {
			return HttpGet.METHOD_NAME;
		}
	}
	
}
