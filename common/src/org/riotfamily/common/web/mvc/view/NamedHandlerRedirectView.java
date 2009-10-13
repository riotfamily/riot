/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.web.mvc.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mvc.mapping.HandlerUrlResolver;
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
	public void render(Map<String, ?> model, HttpServletRequest request, 
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
