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
package org.riotfamily.website.view;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.mapping.HandlerUrlResolver;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.servlet.StringCapturingResponseWrapper;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.website.hyphenate.RiotHyphenator;
import org.riotfamily.website.performance.ResourceStamper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.MethodInvoker;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
	* @author Felix Gnass [fgnass at neteye dot de]
	* @since 6.5
	*/
public class CommonMacroHelper {

	private RiotLog log = RiotLog.get(CommonMacroHelper.class);

	private static final Pattern LINK_PATTERN = Pattern.compile(
			"(\\s+href\\s*=\\s*\")(.+?)(\")", Pattern.CASE_INSENSITIVE);
	
	private static final Pattern TEXT_PATTERN = Pattern.compile(
			"([^<]*)(<[^>]+>|$)");
	
	private static Random random = new Random();

	private Date currentTime;
	
	private ApplicationContext ctx;
	
	private HttpServletRequest request;

	private HttpServletResponse response;

	private ServletContext servletContext;
	
	private ResourceStamper stamper;

	private HandlerUrlResolver handlerUrlResolver;
	
	private RiotHyphenator hyphenator;
	
	private boolean compressResources;
	
	private Locale requestLocale = null;
	
	private String dateFormat;
	
	private String dateDelimiter;

	public CommonMacroHelper(ApplicationContext ctx,
			HttpServletRequest request, HttpServletResponse response, 
			ServletContext servletContext, ResourceStamper stamper, 
			HandlerUrlResolver handlerUrlResolver,
			RiotHyphenator hyphenator, boolean compressResources) {

		this.ctx = ctx;
		this.request = request;
		this.response = response;
		this.servletContext = servletContext;
		this.stamper = stamper;
		this.handlerUrlResolver = handlerUrlResolver;
		this.hyphenator = hyphenator;
		this.compressResources = compressResources;
	}

	public Random getRandom() {
		return random;
	}
	
	public boolean isCompressResources() {
		return compressResources;
	}

	public Date getCurrentTime() {
		if (currentTime == null) {
			currentTime = new Date();
		}
		return currentTime;
	}
	
	public Locale getLocale() {
		if (requestLocale == null) {
			requestLocale = RequestContextUtils.getLocale(request);
		}
		return requestLocale;
	}
	
	public String getDateFormat() {
		if (dateFormat == null) {			
			dateFormat = FormatUtils.getDateFormat(getLocale());			
		}
		return dateFormat;
	}
	
	public String getDateDelimiter() {		
		if (dateDelimiter == null) {
			if (dateFormat == null) {
				getDateFormat();
			}
			dateDelimiter = FormatUtils.getDateDelimiter(dateFormat);			
		}
		return dateDelimiter;
	}
	
	public String getMessage(String code, Collection<?> args) {
		return ctx.getMessage(code.trim(), args.toArray(), null, getLocale());
	}
	
	public String getMessageWithDefault(String code, String defaultMessage, 
			Collection<?> args, Locale locale) {
		
		if (!StringUtils.hasText(defaultMessage)) {
			defaultMessage = null;
		}
		else {
			defaultMessage = FormatUtils.stripWhitespaces(defaultMessage);
		}
		return ctx.getMessage(code.trim(), args.toArray(), defaultMessage, locale);
	}
	
	public String getMessage(MessageSourceResolvable resolvable) {
		return ctx.getMessage(resolvable, getLocale());
	}
		
	public String resolveUrl(String url) {
		return ServletUtils.resolveUrl(url, request);
	}
	
	public String resolveAndEncodeUrl(String url) {
		return ServletUtils.resolveAndEncodeUrl(url, request, response);
	}

	public String localize(String path) {
		String ext = FormatUtils.getExtension(path);
		String prefix = path;
		if (ext.length() > 0) {
			ext = "." + ext;
			prefix = path.substring(0, path.length() - ext.length()) + "_";
		}
		String localized = prefix + getLocale() + ext;
		if (fileExists(localized)) {
			return localized;
		}
		localized = prefix + getLocale().getLanguage() + ext;
		if (fileExists(localized)) {
			return localized;
		}
		return path;
	}
	
	private boolean fileExists(String path) {
		String realPath = servletContext.getRealPath(path);
		if (realPath == null) {
			return false;
		}
		return new File(realPath).exists();
	}

	public String resolveAndEncodeLinks(String html) {
		Matcher m = LINK_PATTERN.matcher(html);
		StringBuffer result = new StringBuffer();
		while (m.find() && m.groupCount() == 3) {
			String newLink = ServletUtils.resolveAndEncodeUrl(m.group(2), request, response);
			log.debug("Replacing link '" + m.group(2) + "' with '" + newLink + "'");
			m.appendReplacement(result, m.group(1) + newLink + m.group(3));
		}
		m.appendTail(result);
		return result.toString();
	}
	
	public String getAbsoluteUrl(String url) {
		return ServletUtils.getAbsoluteUrlPrefix(request)
				.append(request.getContextPath()).append(url).toString();
	}

	public String getUrlForHandler(String handlerName, Object attributes) {
		return handlerUrlResolver.getUrlForHandler(request, handlerName, attributes);
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

	public String setParameter(String url, String name, String value) {
		return ServletUtils.setParameter(url, name, value);
	}

	public String addParameter(String url, String name, String value) {
		return ServletUtils.addParameter(url, name, value);
	}

	public String addRequestParameters(String url) {
		return ServletUtils.addRequestParameters(url, request);
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
	
	public String getHostName(String url) {
		try {
			URI uri = new URI(url);
			return uri.getHost();
		}
		catch (URISyntaxException e) {
			log.warn(e.getMessage());
		}
		return null;
	}

	public String include(String url) throws ServletException, IOException {
		//request.getRequestDispatcher(url).include(request, response);
		return capture(url);
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
	
	public String addCurrentTimestamp(String s) {
		return stamper.stamp(s, true);
	}

	/**
     * Partitions the given collection by inspecting the specified property
     * of the contained items.
     *
     * @param c The collection to partition
     * @param titleProperty The property to use for grouping
     * @return A list of {@link ObjectGroup ObjectGroups}
     */
    public<T> List<ObjectGroup<?, T>> partition(Collection<T> c, String titleProperty) {
		ArrayList<ObjectGroup<?, T>> groups = Generics.newArrayList();
		ObjectGroup<Object, T> group = null;
		for (T item : c) {
			Object title = PropertyUtils.getProperty(item, titleProperty);
			if (group == null || (title != null && !title.equals(group.getTitle()))) {
				group = ObjectGroup.newInstance(title, item, false);
				groups.add(group);
			}
			else {
				group.add(item);
			}
		}
		return groups;
	}
    
    public Object invokeStaticMethod(String method, Collection<?> args) 
    		throws ClassNotFoundException, NoSuchMethodException, 
    		InvocationTargetException, IllegalAccessException {
    	
    	MethodInvoker methodInvoker = new MethodInvoker();
    	methodInvoker.setStaticMethod(method);
    	methodInvoker.setArguments(args.toArray());
    	methodInvoker.prepare();
    	return methodInvoker.invoke();
    }
    
    public String toDelimitedString(Collection<?> c, String delim) {
    	return StringUtils.collectionToDelimitedString(c, delim);
    }

    /**
     * Shuffles the given collection
     * 
     * @param collection The collection to shuffle
     * @return The shuffled collection
     */
	public List<?> shuffle(Collection<?> collection) {
		List<?> result = new ArrayList<Object>(collection);
		Collections.shuffle(result);
		return result;
	}    

	public String getFileExtension(String filename, 
			Collection<String> validExtensions,
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

	public String formatNumber(Number number, String pattern, String localeString) {
		Locale locale = StringUtils.hasText(localeString) 
				? StringUtils.parseLocaleString(localeString)
				: Locale.US;
				
		return FormatUtils.formatNumber(number, pattern, locale);
	}
	
	public int round(float number) {
		return Math.round(number);
	}
	
	public String hyphenatePlainText(String text) {
		return hyphenator.hyphenate(getLocale(), text);
	}
	
	public String hyphenate(String markup) {
		StringBuffer sb = new StringBuffer();
		Matcher matcher = TEXT_PATTERN.matcher(markup);
		while (matcher.find()) {
			String text = matcher.group(1);
			if (text.length() > 0) {
				sb.append(hyphenatePlainText(text));
			}
			sb.append(matcher.group(2));
		}
		return sb.toString();
	}
	
	public String toTitleCase(String s) {
		return FormatUtils.fileNameToTitleCase(s);
	}
	
	public String stripTagsAndWhitespaces(String s) {
		return FormatUtils.stripWhitespaces(FormatUtils.stripTags(s));
	}
		
	public String xmlEscapeDanglingAmps(String s) {
		return FormatUtils.xmlEscapeDanglingAmps(s);
	}
		
	public String toJSON(Object obj) {
		return JSONObject.fromObject(obj).toString();
	}
	
	/**
	 * Splits a list into a specified number of groups. The items are 
	 * distributed evenly. Example:
	 * <pre>
	 * 1 | 4 | 7
	 * 2 | 5 | 8
	 * 3 | 6
	 * </pre>
	 * @param items The items to split
	 * @param groups The number of groups (NOT number of group-items)
	 * @return The splitted list
	 */
	public<T> List<List<T>> split(List<T> items, int groups) {
		if (items == null) {
			return null;
		}
		List<List<T>> result = new ArrayList<List<T>>();
		int itemsSize = items.size();
		int remainder = itemsSize % groups;
		int maxGroupMembers = itemsSize / groups;
		if (remainder > 0) {
			++maxGroupMembers;
		}
		int currentIndex = 0;
		for (int i = 0; i < groups; i++) {
			int currentGroupMembers = maxGroupMembers;
			if (remainder > 0 && i >= remainder) {
				--currentGroupMembers;
			}
			List<T> currentList = new ArrayList<T>();
			for (int j = 0; j < currentGroupMembers; j++) {
				currentList.add(items.get(currentIndex++));
			}
			result.add(currentList);
		}
		return result;
	}
		
	public ExposeAsVariablesDirective getExposeAsVariablesDirective() {
		return new ExposeAsVariablesDirective();
	}
	
	public static class ExposeAsVariablesDirective implements TemplateDirectiveModel {
		
		@SuppressWarnings("unchecked")
		public void execute(Environment env, Map params,
				TemplateModel[] loopVars, TemplateDirectiveBody body)
				throws TemplateException, IOException {
			
			Object param = params.values().iterator().next();
			if (param instanceof Map) {
				ObjectWrapper objectWrapper = env.getConfiguration().getObjectWrapper();
				Map map = (Map) param;
				Iterator it = map.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					TemplateModel model = objectWrapper.wrap(entry.getValue());
					env.setVariable((String) entry.getKey(), model); 
				}
			}
		}
		
	}
}
