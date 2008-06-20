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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.support.Headers;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Abstract CacheHandler that supports zipped content. 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class ZippedResponseHandler extends ResponseCapturingHandler {

	private static Pattern IE_MAJOR_VERSION_PATTERN = 
			Pattern.compile("^Mozilla/\\d\\.\\d+ \\(compatible[-;] MSIE (\\d)");

	private static Pattern BUGGY_NETSCAPE_PATTERN = 
			Pattern.compile("^Mozilla/4\\.0[678]");
	
	private boolean shouldZip;
	
	private boolean zip;
	
	public ZippedResponseHandler(HttpServletRequest request, 
			HttpServletResponse response, 
			CacheKeyAugmentor cacheKeyAugmentor) {
		
		super(request, response, cacheKeyAugmentor);
		shouldZip = responseShouldBeZipped();
		zip = shouldZip && responseCanBeZipped();
	}

	protected void augmentCacheKey(StringBuffer key) {
		if (zip) {
			key.append(".gz");
		}
		super.augmentCacheKey(key);
	}

	protected abstract boolean responseShouldBeZipped();
	
	protected final void postProcess(CacheItem cacheItem) throws IOException {
		if (cacheItem.getSize() > 0) {
			if (shouldZip) {
				Headers headers = cacheItem.getHeaders();
				headers.set("Vary", "Accept-Encoding, User-Agent");
				if (zip) {
					cacheItem.gzipContent();
					headers.set("Content-Encoding", "gzip");
				}
			}					
		}
	}
		
	
	/**
	 * Checks whether the response can be compressed. This is the case when
	 * {@link #clientAcceptsGzip(HttpServletRequest) the client accepts gzip 
	 * encoded content}, the {@link #userAgentHasGzipBugs(HttpServletRequest) 
	 * user-agent has no known gzip-related bugs} and the request is not an 
	 * {@link WebUtils#isIncludeRequest(javax.servlet.ServletRequest)
	 * include request}.
	 */
	protected boolean responseCanBeZipped() {
		return clientAcceptsGzip() 
				&& !userAgentHasGzipBugs()
				&& !WebUtils.isIncludeRequest(getRequest());
	}
	
	/**
	 * Returns whether the Accept-Encoding header contains "gzip".
	 */
	@SuppressWarnings("unchecked")
	protected boolean clientAcceptsGzip() {
		Enumeration values = getRequest().getHeaders("Accept-Encoding");
		if (values != null) {
			while (values.hasMoreElements()) {
				String value = (String) values.nextElement();
				if (value.indexOf("gzip") != -1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns whether the User-Agent has known gzip-related bugs. This is true
	 * for Internet Explorer &lt; 6.0 SP2 and Mozilla 4.06, 4.07 and 4.08. The
	 * method will also return true if the User-Agent header is not present or
	 * empty.
	 */
	protected boolean userAgentHasGzipBugs() {
		String ua = getRequest().getHeader("User-Agent");
		if (!StringUtils.hasLength(ua)) {
			return true;
		}
		Matcher m = IE_MAJOR_VERSION_PATTERN.matcher(ua);
		if (m.find()) {
			int major = Integer.parseInt(m.group(1));
			if (major > 6) {
				// Bugs are fixed in IE 7 
				return false;
			}
			if (ua.indexOf("Opera") != -1) {
				// Opera has no known gzip bugs
				return false;
			}
			if (major == 6) {
				// Bugs are fixed in Service Pack 2 
				return ua.indexOf("SV1") == -1;
			}
			// All other version are buggy.
			return true;
		}
		return BUGGY_NETSCAPE_PATTERN.matcher(ua).find();
	}
}
