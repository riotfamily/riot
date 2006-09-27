package org.riotfamily.pages.page;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.common.web.view.RedirectView;
import org.riotfamily.pages.component.preview.DefaultViewModeResolver;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.MemberBinderAware;
import org.riotfamily.pages.member.WebsiteMember;
import org.riotfamily.pages.member.support.NullMemberBinder;
import org.riotfamily.pages.page.support.PageUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that performs a pageMap lookup for a given request and delegates
 * the the call to the <code>handleRequest</code> method of the controller that
 * is associated with the resolved page. 
 */
public class PageController implements Controller, MemberBinderAware {

	protected Log log = LogFactory.getLog(getClass());
	
	private ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper(true);
	
	private PageMap pageMap;
	
	private String pageAttribute;
		
	private String loginUrl;
	
	private MemberBinder memberBinder = new NullMemberBinder();
	
	ViewModeResolver viewModeResolver = new DefaultViewModeResolver();
	
	public PageController(PageMap map) {
		this.pageMap = map;
	}

	public void setPageAttribute(String pageAttribute) {
		this.pageAttribute = pageAttribute;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
	
	protected String getLoginUrl(HttpServletRequest request) {
		return loginUrl;
	}

	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}
	
	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	protected PageMap getPageMap() {
		return this.pageMap;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		String path = servletMappingHelper.getLookupPathForRequest(request);
		if (log.isDebugEnabled()) {
			log.debug("Looking up handler for [" + path + "]");
		}
		PageAndController pc = getPageAndController(request, path);
		if (pc == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		else {
			return handlePageAndController(request, response, pc);
		}
	}
		
	protected PageAndController getPageAndController(
			HttpServletRequest request, String path) {
		
		return pageMap.getPageAndController(path);
	}
	
	protected ModelAndView handlePageAndController(HttpServletRequest request, 
			HttpServletResponse response, PageAndController pc) 
			throws Exception {
		
		Page page = pc.getPage();
		PageUtils.exposePage(request, page);
		PageUtils.exposePageMap(request, pageMap);
		if (pageAttribute != null) {
			request.setAttribute(pageAttribute, page);
		}
		WebsiteMember member = memberBinder.getMember(request);
		if (page != null) {
			/*
			request.setAttribute(ComponentEditor.INSTANT_PUBLISH_ATTRIBUTE,
					Boolean.valueOf(page.isNew()));
			*/
			if (!page.isAccessible(request, member)) {
				String loginUrl = getLoginUrl(request);
				if (loginUrl == null || member != null) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return null;
				}
				else {
					return new ModelAndView(new RedirectView(
							loginUrl, true, true));
				}
			}
			else if (!page.isPublished()) {
				if (!viewModeResolver.isPreviewMode(request)) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return null;
				}
			}
		}
		Controller controller = pc.getController();
		return controller.handleRequest(request, response);
	}
	
}
