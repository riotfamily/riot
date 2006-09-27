package org.riotfamily.pages.page.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.OncePerRequestInterceptor;
import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;

public class ExposeRootPageInterceptor extends OncePerRequestInterceptor {

	public static final String DEFAULT_ATTRIBUTE = "rootPage";
	
	private PageMap pageMap;
	
	private String attribute = DEFAULT_ATTRIBUTE;
	
	private ServletMappingHelper mappingHelper = new ServletMappingHelper(true);
	
	public ExposeRootPageInterceptor(PageMap pageMap) {
		this.pageMap = pageMap;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	protected boolean preHandleOnce(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		String path = mappingHelper.getLookupPathForRequest(request);
		int i = path.indexOf('/', 1);
		String rootPath = path.substring(0, i != -1 ? i : path.length());
		Page rootPage = pageMap.getPage(rootPath);
		request.setAttribute(attribute, rootPage);
		return true;
	}
}
