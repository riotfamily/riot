/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.crawler;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.log.RiotLog;

/**
 * PageLoader implementation that uses the Jakarta Commons HttpClient.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CommonsHttpClientPageLoader implements PageLoader {

	private RiotLog log = RiotLog.get(CommonsHttpClientPageLoader.class);

    private HttpClient client = new HttpClient();

    private boolean textHtmlOnly = true;

	public void setTextHtmlOnly(boolean textHtmlOnly) {
		this.textHtmlOnly = textHtmlOnly;
	}

	public PageData loadPage(Href href) {
		String url = href.getResolvedUri();
		PageData pageData = new PageData(href);
		log.info("Loading page: " + url);
		GetMethod method = new GetMethod(url);
		HttpMethodRetryHandler retryHandler = new DefaultHttpMethodRetryHandler();
		HttpMethodParams params = new HttpMethodParams();
		params.setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);
		method.setParams(params);
		method.setFollowRedirects(false);

		try {
			int statusCode = client.executeMethod(method);
			pageData.setStatusCode(statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				if (accept(method)) {
					pageData.setContent(method.getResponseBodyAsStream(),
							method.getResponseCharSet());
					
					Header[] headers = method.getResponseHeaders();
					for (int i = 0; i < headers.length; i++) {
						pageData.addHeader(headers[i].getName(), headers[i].getValue());
					}
				}
			}
			else {
				log.info("Status: " + statusCode);
				Header location = method.getResponseHeader("Location");
				if (location != null) {
					pageData.setRedirectUrl(location.getValue());
				}
				else {
					pageData.setError(method.getStatusText());
				}
			}
		}
		catch (Exception e) {
			pageData.setError(e.getMessage());
			log.warn(e.getMessage());
		}
		finally {
			try {
				method.releaseConnection();
			}
			catch (Exception e) {
			}
		}
		return pageData;
	}

	protected boolean accept(GetMethod method) {
		if (textHtmlOnly) {
			Header contentType = method.getResponseHeader("Content-Type");
			String mimeType = contentType.getValue();
			return mimeType != null && mimeType.startsWith("text/html");
		}
		return true;
	}

}
