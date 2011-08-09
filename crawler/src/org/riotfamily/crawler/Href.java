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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Representation of a hypertext reference.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class Href {

	private static Pattern fragmentPattern = Pattern.compile("#.*$");

	private static Pattern jsessionIdPattern = 
			Pattern.compile(";jsessionid=[^?#]*");

	private String baseUri;
	
	private String uri;
	
	private String resolvedUri;
	
	private String referrerUrl;
	
	public Href(String baseUri, String uri, String referrerUrl) {		
		this.baseUri = baseUri;
		this.uri = uri;
		this.resolvedUri = stripSessionId(stripFragment(resolveLink(baseUri, uri)));
		this.referrerUrl = referrerUrl != null ? stripSessionId(referrerUrl) : null;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public String getUri() {
		return uri;
	}
	
	public String getResolvedUri() {
		return resolvedUri;
	}
	
	public String getReferrerUrl() {
		return referrerUrl;
	}
	
	public int hashCode() {		
		return resolvedUri == null ? 0 : resolvedUri.hashCode();		
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Href) {
			Href other = (Href) obj;
			return ObjectUtils.nullSafeEquals(resolvedUri, other.resolvedUri);
		}
		return false;
	}
	
	public String toString() {
		return resolvedUri;
	}
	
	private static String stripFragment(String url) {
		return fragmentPattern.matcher(url).replaceAll("");
	}

	private static String stripSessionId(String url) {
		return jsessionIdPattern.matcher(url).replaceAll("");
	}
	
	private static String resolveLink(String baseUrl, String href) {
		try {
			if (href.startsWith("?")) {
				Assert.notNull(baseUrl);
				int i = baseUrl.indexOf('?');
				if (i >= 0) {
					return baseUrl.substring(0, i) + href;
				}
				else {
					return baseUrl + href;
				}
			}
			if (baseUrl == null) {
				return href;
			}
			URI baseURI = new URI(baseUrl);
			return baseURI.resolve(href).toString();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException e) {
			return href;
		}
	}
}
