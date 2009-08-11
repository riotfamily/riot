package org.riotfamily.common.mapping;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * Class that performs URL lookups for handlers mapped by a 
 * {@link ReverseHandlerMapping}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HandlerUrlResolver implements ApplicationContextAware {

	private List<ReverseHandlerMapping> mappings;
	
	private HandlerUrlResolver parent;
	
	private ApplicationContext applicationContext;
	
	
	public void setParent(HandlerUrlResolver parent) {
		this.parent = parent;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	private List<ReverseHandlerMapping> getMappings() {
		if (mappings == null) {
			Assert.notNull(applicationContext, "The ApplicationContext must be set first");
			mappings = SpringUtils.orderedBeans(applicationContext, 
					ReverseHandlerMapping.class);
		}
		return mappings;
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 */
	public String getUrlForHandler(String handlerName, Object... attributes) {
		
		Object attr;
		if (attributes.length == 0) {
			attr = null;
		}
		else if (attributes.length == 1) {
			attr = attributes[0];
		}
		else {
			attr = attributes;
		}
		for (ReverseHandlerMapping mapping : getMappings()) {
			String url = mapping.getUrlForHandler(handlerName, attr);
			if (url != null) {
				return url;
			}
		}
		if (parent != null) {
			return parent.getUrlForHandler(handlerName, attributes);
		}
		return null;
	}
	
}
