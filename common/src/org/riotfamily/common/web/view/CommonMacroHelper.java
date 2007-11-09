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
package org.riotfamily.common.web.view;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.collaboration.SharedProperties;
import org.riotfamily.common.web.filter.ResourceStamper;
import org.riotfamily.common.web.mapping.ReverseHandlerMapping;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.common.web.util.StringCapturingResponseWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
	* @author Felix Gnass [fgnass at neteye dot de]
	* @since 6.5
	*/
public class CommonMacroHelper {

	private static final Log log = LogFactory.getLog(CommonMacroHelper.class);

	private static Random random = new Random();

	private ApplicationContext ctx;
	
	private HttpServletRequest request;

	private HttpServletResponse response;

	private ResourceStamper stamper;

	private List mappings;
	
	private Locale requestLocale = null;

	public CommonMacroHelper(ApplicationContext ctx,
			HttpServletRequest request, HttpServletResponse response, 
			ResourceStamper stamper, List mappings) {

		this.ctx = ctx;
		this.request = request;
		this.response = response;
		this.stamper = stamper;
		this.mappings = mappings;
	}

	public Random getRandom() {
		return random;
	}

	public Locale getLocale() {
		if (requestLocale == null) {
			requestLocale = RequestContextUtils.getLocale(request);
		}
		return requestLocale;
	}
	
	public String getMessage(String code, List args, String defaultMessage) {
		return ctx.getMessage(code, args.toArray(), defaultMessage, getLocale());
	}
	
	public String getMessage(MessageSourceResolvable resolvable) {
		return ctx.getMessage(resolvable, getLocale());
	}
	
	public String getSharedProperty(String key) {
		return SharedProperties.getProperty(request, key);
	}
	
	public String setSharedProperty(String key, String value) {
		SharedProperties.setProperty(request, key, value);
		return "";
	}
	
	public String resolveAndEncodeUrl(String url) {
		return ServletUtils.resolveAndEncodeUrl(url, request, response);
	}

	public String getAbsoluteUrl(String url) {
		return ServletUtils.getAbsoluteUrlPrefix(request)
				.append(request.getContextPath()).append(url).toString();
	}

	public String getUrlForHandler(String handlerName, 
			Object attributes, String prefix) {
		
		String url = null;
		Iterator it = mappings.iterator();
		while (url == null && it.hasNext()) {
			ReverseHandlerMapping mapping = (ReverseHandlerMapping) it.next();
			url = mapping.getUrlForHandler(
					handlerName, prefix, attributes, request);
		}
		return url;
	}
		
	public String getOriginatingRequestUri() {
		String uri = ServletUtils.getOriginatingRequestUri(request);
		if (StringUtils.hasText(request.getQueryString())) {
			uri = uri + "?" + request.getQueryString();
		}
 		return uri;
	}
	
	public String getPathWithinApplication() {
		return ServletUtils.getPathWithinApplication(request);
	}
	
	public String getTopLevelHandlerName() {
		return (String) request.getAttribute(
				ReverseHandlerMapping.TOP_LEVEL_HANDLER_NAME_ATTRIBUTE);
	}

	public boolean isExternalUrl(String url) {
		try {
			URI uri = new URI(url);
			if (!uri.isOpaque()) {
				if (uri.isAbsolute() && !request.getServerName().equals(
						uri.getHost())) {

					return true;
				}
			}
		}
		catch (URISyntaxException e) {
			log.warn(e.getMessage());
		}
		return false;
	}

	public String include(String url) throws ServletException, IOException {
		request.getRequestDispatcher(url).include(request, response);
		return "";
	}
	
	public String capture(String url) throws ServletException, IOException {
		StringCapturingResponseWrapper wrapper = 
				new StringCapturingResponseWrapper(response);
		
		request.getRequestDispatcher(url).include(request, wrapper);
		return wrapper.getCapturedData();
	}

	public String addTimestamp(String s) {
		return stamper.stamp(s);
	}

	public List partition(Collection c, String titleProperty) {
		return PropertyUtils.partition(c, titleProperty);
	}

	public String getFileExtension(String filename, Collection validExtensions,
			String defaultExtension) {

		String ext = FormatUtils.getExtension(filename);
		if (validExtensions.isEmpty() || validExtensions.contains(ext)) {
			return ext;
		}
		return defaultExtension;
	}

	public String baseName(String path) {
		int begin = path.lastIndexOf('/') + 1;
		int end = path.indexOf(';');
		if (end == -1) {
			end = path.indexOf('?');
			if (end == -1) {
				end = path.length();
			}
		}
		return path.substring(begin, end);
	}

	public String formatByteSize(long bytes) {
		return FormatUtils.formatByteSize(bytes);
	}
	
	public String formatMillis(long millis) {
		return FormatUtils.formatMillis(millis);
	}

	public String toTitleCase(String s) {
		return FormatUtils.fileNameToTitleCase(s);
	}

}
