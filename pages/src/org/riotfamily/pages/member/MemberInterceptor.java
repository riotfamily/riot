package org.riotfamily.pages.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.OncePerRequestInterceptor;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;

public class MemberInterceptor extends OncePerRequestInterceptor
		implements MemberBinderAware {

	public static final String INTERCEPTED_URL = 
			MemberInterceptor.class.getName() + ".interceptedUrl"; 
	
	private String loginUrl;
	
	private MemberBinder memberBinder;
	
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	protected boolean preHandleOnce(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
	
		Page page = PageUtils.getPage(request);
		WebsiteMember member = memberBinder.getMember(request);
		if (!page.isAccessible(request, member)) {
			if (loginUrl != null) {
				String url = request.getRequestURI();
				request.getSession().setAttribute(INTERCEPTED_URL, url);
				response.sendRedirect(request.getContextPath() + loginUrl);
			}
			else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
			return false;
		}
		return true;
	}

}
