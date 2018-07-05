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
package org.riotfamily.crawler;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * PageLoader implementation that uses the Jakarta Commons HttpClient.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class HttpClientPageLoader implements PageLoader {

	private Logger log = LoggerFactory.getLogger(HttpClientPageLoader.class);

    private CloseableHttpClient client =  HttpClients.custom().disableRedirectHandling().build();

    private boolean textHtmlOnly = true;

	public void setTextHtmlOnly(boolean textHtmlOnly) {
		this.textHtmlOnly = textHtmlOnly;
	}

	public PageData loadPage(Href href) {
		String url = href.getResolvedUri();
		PageData pageData = new PageData(href);
		log.info("Loading page: " + url);
		HttpGet get = null;
		try {
			get = new HttpGet(url);
			if (StringUtils.hasText(href.getReferrerUrl())) {
				get.addHeader(ServletUtils.REFERER_HEADER, href.getReferrerUrl());
			}
			prepareMethod(get);
			CloseableHttpResponse httpResponse = client.execute(get);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			pageData.setStatusCode(statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				try {
					HttpEntity entity = httpResponse.getEntity();
					if (accept(entity)) {
						String content = EntityUtils.toString(entity, Consts.UTF_8);
						pageData.setHtml(content);
						Header[] headers = httpResponse.getAllHeaders();
						for (int i = 0; i < headers.length; i++) {
							pageData.addHeader(headers[i].getName(), headers[i].getValue());
						}
					}
				}
				finally {
					httpResponse.close();
				}
			}
			else {
				log.info("Status: " + statusCode);
				Header[] locationHeaders = httpResponse.getHeaders("Location");
				if (locationHeaders != null && locationHeaders.length == 1) {
					pageData.setRedirectUrl(locationHeaders[0].getValue());
				}
				else {
					pageData.setError(httpResponse.getStatusLine().toString());
				}
			}
		}
		catch (Exception e) {
			pageData.setError(e.getMessage());
			log.warn(e.getMessage());
		}
		finally {
			try {
				if (get != null) {
					get.releaseConnection();
				}
			}
			catch (Exception e) {
			}
		}
		return pageData;
	}
	
	protected void prepareMethod(HttpRequestBase method) {
	}

	protected boolean accept(HttpEntity entity) {
		if (textHtmlOnly) {
			Header contentType = entity.getContentType();
			String mimeType = contentType.getValue();
			return mimeType != null && mimeType.startsWith("text/html");
		}
		return true;
	}

}
