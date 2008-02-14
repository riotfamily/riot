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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.riotfamily.common.web.mapping.HandlerUrlResolver;
import org.riotfamily.common.web.mapping.ReverseHandlerMapping;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.list.command.CommandContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Command that uses Riot's {@link ReverseHandlerMapping} feature to build
 * a preview URL for a bean.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PreviewCommand extends PopupCommand 
		implements ServletContextAware {

	public static final String STYLE_CLASS = "link";
	
	private String handlerName;
	
	private String servletName = "website";
	
	private Properties wildcardProperties;
	
	private ServletContext servletContext;
	
	private HandlerUrlResolver handlerUrlResolver;
	
	/**
	 * Sets the name of the handler to be used. The handler must be mapped 
	 * using a {@link ReverseHandlerMapping}. If no handlerName is explicitly
	 * set, Riot uses the {@link EditorDefinition#getId() editorId} by 
	 * convention. 
	 */
	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	/**
	 * Sets the mapping from wildcard-names to property-names. If not 
	 * specified, the whole bean is passed to the {@link HandlerUrlResolver},
	 * assuming that all wildcards have a corresponding property. 
	 * @param wildcardProperties Properties instance with the name of the 
	 * 		wildcard as key and the property-name as value
	 */
	public void setWildcardProperties(Properties wildcardProperties) {
		this.wildcardProperties = wildcardProperties;
	}

	/**
	 * Sets the name of the DispatcherServlet that provides the preview handler.
	 * Default is <code>website</code>.
	 */
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	private WebApplicationContext getWebsiteApplicationContext() {
		String contextAttribute = DispatcherServlet.SERVLET_CONTEXT_PREFIX 
				+ servletName;
		
		WebApplicationContext ctx = (WebApplicationContext) 
				servletContext.getAttribute(contextAttribute);
		
		Assert.state(ctx != null, "No WebApplicationContext found in the " +
				"ServletContext under the key '" + contextAttribute + "'. " +
				"Make sure your DispatcherServlet is called '" + 
				servletName + "' and publishContext is set to true.");
		
		return ctx;
	}
	
	private HandlerUrlResolver getHandlerUrlResolver() {
		if (handlerUrlResolver == null) {
			handlerUrlResolver = (HandlerUrlResolver) 
					getWebsiteApplicationContext().getBean("handlerUrlResolver", 
					HandlerUrlResolver.class);
		}
		return handlerUrlResolver;
	}
	
	protected String getUrl(CommandContext context) {
		if (handlerName == null) {
			if (context.getListDefinition().getDisplayDefinition() != null) {
				handlerName = context.getListDefinition().getDisplayDefinition().getId();
			}
			else {
				handlerName = context.getListDefinition().getId();
			}
			log.info("No handlerName specified - using '" + handlerName 
					+ "' by convention.");
		}
		String url = getHandlerUrlResolver().getUrlForHandler(
				context.getRequest(), handlerName, 
				getAttributes(context.getBean()), null);
		
		Assert.notNull(url, "Could not resolve the URL for handler '" 
				+ handlerName + "'");
		
		return context.getRequest().getContextPath() + url;
	}
	
	protected Object getAttributes(Object bean) {
		if (wildcardProperties == null) {
			return bean;
		}
		BeanWrapper wrapper = new BeanWrapperImpl(bean);
		HashMap attributes = new HashMap();
		Enumeration names = wildcardProperties.propertyNames();
		while (names.hasMoreElements()) {
			String wildcard = (String) names.nextElement();
			String property = wildcardProperties.getProperty(wildcard);
			attributes.put(wildcard, wrapper.getPropertyValue(property));
		}
		return attributes;
	}
	
	protected String getStyleClass(CommandContext context, String action) {
		return STYLE_CLASS;
	}
}
