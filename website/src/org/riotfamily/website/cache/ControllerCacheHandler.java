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
package org.riotfamily.website.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.AbstractHttpHandler;
import org.riotfamily.common.view.ViewResolverHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ControllerCacheHandler extends AbstractHttpHandler {

	private CacheableController controller;
	
	private ViewResolverHelper viewResolverHelper;
	
	public ControllerCacheHandler(HttpServletRequest request, 
			HttpServletResponse response, CacheableController controller,
			CacheKeyAugmentor cacheKeyAugmentor,
			ViewResolverHelper viewResolverHelper) {

		super(request, response);
		this.controller = controller;
		this.viewResolverHelper = viewResolverHelper;
	}

	@Override
	public String getCacheKey() {
		return controller.getCacheKey(getRequest());
	}
	
	@Override
	public long getLastModified() {
		return controller.getLastModified(getRequest());
	}

	protected boolean responseShouldBeZipped() {
		if (controller instanceof Compressible) {
			return ((Compressible) controller).gzipResponse(getRequest());
		}
		return false;
	}
	
	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mv = controller.handleRequest(getRequest(), response);
		if (mv != null) {
	    	View view = viewResolverHelper.resolveView(getRequest(), mv);
	    	view.render(mv.getModel(), getRequest(), response);
	    }
	}

}
