package org.riotfamily.website.view;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.mapping.HandlerUrlResolver;
import org.riotfamily.common.view.MacroHelperFactory;
import org.riotfamily.website.hyphenate.RiotHyphenator;
import org.riotfamily.website.performance.ResourceStamper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CommonMacroHelperFactory implements MacroHelperFactory, 
		ApplicationContextAware, ServletContextAware {

	private ApplicationContext applicationContext;

	private ServletContext servletContext;
	
	private ResourceStamper stamper;
	
	private HandlerUrlResolver handlerUrlResolver;
	
	private RiotHyphenator hyphenator;
	
	private boolean compressResources = false;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setStamper(ResourceStamper stamper) {
		this.stamper = stamper;
	}
	
	public void setHandlerUrlResolver(HandlerUrlResolver handlerUrlResolver) {
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	public void setHyphenator(RiotHyphenator hyphenator) {
		this.hyphenator = hyphenator;
	}

	public void setCompressResources(boolean compressResources) {
		this.compressResources = compressResources;
	}
	
	public Object createMacroHelper(HttpServletRequest request, 
			HttpServletResponse response, Map<String, ?> model) {
		
		return new CommonMacroHelper(applicationContext, request, response, 
				servletContext, stamper, handlerUrlResolver, hyphenator, 
				compressResources);
	}

}
