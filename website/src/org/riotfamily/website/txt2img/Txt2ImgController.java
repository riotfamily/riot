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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.txt2img;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.cachius.spring.Compressible;
import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.compressor.YUIJavaScriptCompressor;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Txt2ImgController extends AbstractCacheableController
		implements LastModified, Compressible {

	private static final Resource SCRIPT_RESOURCE = new ClassPathResource(
			"txt2img.js", Txt2ImgController.class);

	private static final Resource PIXEL_RESOURCE = new ClassPathResource(
			"pixel.gif", Txt2ImgController.class);

	private long lastModified = System.currentTimeMillis();

	
	private Map generators = new HashMap();
	
	private List selectors = new ArrayList();

	private ImageGenerator defaultGenerator = new ImageGenerator();

	private YUIJavaScriptCompressor compressor = new YUIJavaScriptCompressor();
	
	private Pattern refererPattern;

	
	/**
	 * @param compressor the compressor to set
	 */
	public void setCompressor(YUIJavaScriptCompressor compressor) {
		this.compressor = compressor;
	}
	
	/**
	 * Sets a List of {@link ReplacementRule} objects.
	 */
	public void setRules(List rules) {
		Iterator it = rules.iterator();
		while (it.hasNext()) {
			ReplacementRule rule = (ReplacementRule) it.next();
			addRule(rule);
		}
	}

	public void addRule(ReplacementRule rule) {
		String[] sel = StringUtils.commaDelimitedListToStringArray(rule.getSelector());
		for (int i = 0; i < sel.length; i++) {
			selectors.add(sel[i]);
			generators.put(sel[i], rule);
		}
	}
	
	/**
	 * Sets the generator to be used if no generator is found for a given
	 * CSS selector.
	 */
	public void setDefaultGenerator(ImageGenerator defaultGenerator) {
		this.defaultGenerator = defaultGenerator;
	}

	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		String queryString = request.getQueryString();
		if (queryString != null) {
			key.append('?').append(queryString);
		}
	}

	public boolean gzipResponse(HttpServletRequest request) {
		String extension = FormatUtils.getExtension(request.getRequestURI());
		return extension.equals("js");
	}
	
	public long getLastModified(HttpServletRequest request) {
		return lastModified;
	}

	/**
	 * Sets a regular expression that is used to check the Referer header.
	 * You may use this setting to prevent other websites from using your
	 * ImageGenerator.
	 */
	public void setRefererPattern(Pattern refererPattern) {
		this.refererPattern = refererPattern;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (refererPattern != null) {
			String referer = request.getHeader("Referer");
			if (referer != null && !refererPattern.matcher(referer).matches()) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		
		String extension = FormatUtils.getExtension(request.getRequestURI());
		String text = getText(request);
		if (text != null) {
			serveImage(text, request, response);
		}
		else if (extension.equals("gif")) {
			servePixelGif(response);
		}
		else {
			serveScript(request, response);
		}
		return null;
	}

	/**
	 * Returns the locale for the given request. The method first checks for
	 * a parameter called 'locale' an parses it. If the parameter is not set,
	 * Spring's LocaleResolver mechanism is used.
	 */
	protected Locale getLocale(HttpServletRequest request) {
		String localeString = request.getParameter("locale");
		if (StringUtils.hasText(localeString)) {
			return StringUtils.parseLocaleString(localeString);
		}
		return RequestContextUtils.getLocale(request);
	}

	protected String getEncodedParam(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		return value != null ? FormatUtils.uriUnescape(value.replace('@', '%')) : null;
	}

	/**
	 * Returns the text to be rendered. The method unescapes HTML entities
	 * and optionally converts the String to upper or lower case, if the
	 * 'transform' HTTP parameter is set.
	 */
	protected String getText(HttpServletRequest request) {
		String text = getEncodedParam(request, "text");
		if (text != null) {
			text = FormatUtils.stripWhitespaces(text, true);
			text = text.replaceAll("&(shy|#173);", "\t");
			text = HtmlUtils.htmlUnescape(text);
			String transform = request.getParameter("transform");
			if (StringUtils.hasText(transform)) {
				Locale locale = getLocale(request);
				if (transform.equalsIgnoreCase("uppercase")) {
					text = text.toUpperCase(locale);
				}
				if (transform.equalsIgnoreCase("lowercase")) {
					text = text.toLowerCase(locale);
				}
			}
		}
		return text;
	}

	/**
	 * Serves an image containing the given text.
	 */
	protected void serveImage(String text, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		ImageGenerator generator = defaultGenerator;
		String selector = getEncodedParam(request, "selector");
		if (selector != null) {
			generator = (ImageGenerator) generators.get(selector);
		}
		Assert.notNull(generator, "No ImageGenerator found for selector '"
				+ selector + "' and no default generator is set.");

		int maxWidth = ServletRequestUtils.getIntParameter(request, "width", 0);
		if (maxWidth <= 0) {
			maxWidth = Integer.MAX_VALUE;
		}
		String color = getEncodedParam(request, "color");
		response.setContentType("image/png");
		ServletUtils.setFarFutureExpiresHeader(response);
		generator.generate(text, maxWidth, color, response.getOutputStream());
	}

	/**
	 * Serves a JavaScript file that can be used to replace texts on the
	 * client side.
	 */
	protected void serveScript(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		response.setContentType("text/javascript");
		ServletUtils.setFarFutureExpiresHeader(response);
		StringWriter out = new StringWriter(); 
		
		IOUtils.copy(SCRIPT_RESOURCE.getInputStream(), out, "UTF-8");

		out.write("var txt2img = new RiotImageReplacement('");
		out.write(getGeneratorUrl(request));
		out.write("?locale=");
		out.write(RequestContextUtils.getLocale(request).toString());
		out.write("', '");
		out.write(getPixelUrl(request));
		out.write("', [");
		Iterator it = selectors.iterator();
		while (it.hasNext()) {
			out.write("'");
			out.write((String) it.next());
			out.write("'");
			if (it.hasNext()) {
				out.write(", ");
			}
		}
		out.write("]);");
		compressor.compress(new StringReader(out.toString()), 
				response.getWriter());
	}

	private String getGeneratorUrl(HttpServletRequest request) {
		return FormatUtils.stripExtension(ServletUtils.getRequestUri(request)) + ".png";
	}
	
	private String getPixelUrl(HttpServletRequest request) {
		return FormatUtils.stripExtension(ServletUtils.getRequestUri(request)) + ".gif";
	}

	/**
	 * Serves a transparent 1x1 pixel GIF that is needed by the JavaScript
	 * to work around the PNG loading in IE &lt; 7.
	 */
	protected void servePixelGif(HttpServletResponse response) throws IOException {
		response.setContentType("image/gif");
		ServletUtils.setFarFutureExpiresHeader(response);
		IOUtils.copy(PIXEL_RESOURCE.getInputStream(), response.getOutputStream());
	}
}
