package org.riotfamily.common.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.mapping.HandlerUrlResolver;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

/**
 * View that sends a redirect to a named handler.
 * 
 * @see HandlerUrlResolver
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class NamedHandlerRedirectView extends RedirectView {

	private HandlerUrlResolver handlerUrlResolver;
	
	private String handlerName;
	
	public NamedHandlerRedirectView(String handlerName) {
		this(handlerName, null);
	}
	
	public NamedHandlerRedirectView(String handlerName, 
			HandlerUrlResolver handlerUrlResolver) {
		
		this.handlerName = handlerName;
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (handlerUrlResolver == null) {
			WebApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
			handlerUrlResolver = (HandlerUrlResolver) context.getBean("handlerUrlResolver");
		}
		String handlerUrl = handlerUrlResolver.getUrlForHandler(handlerName, model, null);
		Assert.notNull(handlerUrl, "Can't resolve URL for handler " + handlerName);
		setUrl(handlerUrl);
		setContextRelative(true);
		super.render(model, request, response);
	}

}
