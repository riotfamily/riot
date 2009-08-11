package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.cachius.servlet.ZippedResponseHandler;
import org.riotfamily.common.view.ViewResolverHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ControllerCacheHandler extends ZippedResponseHandler {

	private CacheableController controller;
	
	private ViewResolverHelper viewResolverHelper;
	
	public ControllerCacheHandler(HttpServletRequest request, 
			HttpServletResponse response, CacheableController controller,
			CacheKeyAugmentor cacheKeyAugmentor,
			ViewResolverHelper viewResolverHelper) {

		super(request, response, cacheKeyAugmentor);
		this.controller = controller;
		this.viewResolverHelper = viewResolverHelper;
	}

	protected String getCacheKeyInternal() {
		return controller.getCacheKey(getRequest());
	}
	
	@Override
	public long getLastModified() throws Exception {
		return controller.getLastModified(getRequest());
	}

	@Override
	public long getTimeToLive() {
		return controller.getTimeToLive();
	}

	protected boolean responseShouldBeZipped() {
		if (controller instanceof Compressible) {
			return ((Compressible) controller).gzipResponse(getRequest());
		}
		return false;
	}
	
	protected void handleInternal(HttpServletResponse response) throws Exception {
		ModelAndView mv = controller.handleRequest(getRequest(), response);
		if (mv != null) {
	    	View view = viewResolverHelper.resolveView(getRequest(), mv);
	    	view.render(mv.getModel(), getRequest(), response);
	    }
	}

}
