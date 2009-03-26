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
package org.riotfamily.common.log4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.riotfamily.common.util.Generics;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Log4J Layout that uses a FreeMarker template to do the formatting.
 * <p> 
 * The actual formatting is deferred until {@link #getFooter()} is invoked,
 * therefore this layout is only suitable for certain appenders like the 
 * {@link SmartSmtpAppender}.
 * </p>  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class FreeMarkerLayout extends Layout {

	private String templateName = "/org/riotfamily/common/log/layout.ftl";
	
	private String contentType = "text/html";
	
	private Template template;
	
	private Map<String, Object> model = Generics.newHashMap();
	
	private List<LoggingEvent> events = Generics.newArrayList();
	
	/**
	 * Sets the contentType that will be returned by {@link #getContentType()}.
	 * Default is <code>text/html</code>.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * Sets the location in the classpath of the template to use. The default
	 * is <code>/org/riotfamily/common/log/layout.ftl</code>.
	 */
	public void setTemplate(String templateName) {
		this.templateName = templateName;
	}

	public void activateOptions() {
		Configuration cfg = new Configuration();
		cfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "/"));
		try {
			template = cfg.getTemplate(templateName);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * (Re-)initializes the model map. The method always returns <code>null</code>.
	 */
	@Override
	public String getHeader() {
		model.clear();
		events.clear();
		model.put("helper", new MacroHelper());
		model.put("events", events);
		return null;
	}

	/**
	 * Adds the event to the model for later processing and returns an empty String.
	 */
	@Override
	public String format(LoggingEvent event) {
		events.add(event);
		// Initialize the internal fields
		event.getThreadName();
		event.getRenderedMessage();
		event.getNDC();
		event.getMDCCopy();
		event.getThrowableStrRep();
		return "";
	}

	/**
	 * Processes the template and returns the formatted output.
	 */
	@Override
	public String getFooter() {
		try {
			StringWriter sw = new StringWriter();
			template.process(model, sw);
			return sw.toString();
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		catch (TemplateException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean ignoresThrowable() {
		return false;
	}
	
	public static class MacroHelper {
		public Date toDate(long time) {
			return new Date(time);
		}
	}
	
}
