package org.riotfamily.common.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.ServletUtils;

/**
 * View that sends a redirect to the originating request URI.
 * <b>NOTE:</b> The implementation is not thread safe.
 *  
 * @see ServletUtils#getOriginatingRequestUri(HttpServletRequest)
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RedirectAfterPostView extends FlashScopeView {

	@SuppressWarnings("unchecked")
	public void render(Map model, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		setHttp10Compatible(ServletUtils.isHttp10(request));
		setUrl(ServletUtils.getOriginatingRequestUri(request));
		super.render(model, request, response);
	}
	
}
