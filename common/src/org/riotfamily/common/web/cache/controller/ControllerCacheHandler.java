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
package org.riotfamily.common.web.cache.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.AbstractHttpHandler;
import org.riotfamily.common.web.cache.CacheKeyAugmentor;
import org.riotfamily.common.web.mvc.view.ViewResolverHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ControllerCacheHandler extends AbstractHttpHandler {

	private CacheableController controller;

	private CacheKeyAugmentor cacheKeyAugmentor;
	
	private ViewResolverHelper viewResolverHelper;
	
	public ControllerCacheHandler(HttpServletRequest request, 
			HttpServletResponse response, CacheableController controller,
			CacheKeyAugmentor cacheKeyAugmentor,
			ViewResolverHelper viewResolverHelper) {

		super(request, response);
		this.controller = controller;
		this.cacheKeyAugmentor = cacheKeyAugmentor;
		this.viewResolverHelper = viewResolverHelper;
	}

	@Override
	public String getCacheKey() {
		StringBuilder key = new StringBuilder();
		key.append(controller.getCacheKey(getRequest()));
		cacheKeyAugmentor.augmentCacheKey(key, getRequest());
		return key.toString();
	}
	
	@Override
	public long getLastModified() {
		return controller.getLastModified(getRequest());
	}

	@Override
	protected boolean isCompressible() {
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
