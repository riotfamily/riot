package org.riotfamily.pages.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.web.view.RedirectView;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LogoutController implements Controller, MemberBinderAware {

	public static final String SUCCESS_URL_PARAM = "successUrl";
		
	private boolean invalidateSession = false;
	
	private String defaultSucessUrl;
	
	private MemberBinder memberBinder;
	
	public void setDefaultSucessUrl(String welcomeUrl) {
		this.defaultSucessUrl = welcomeUrl;
	}
	
	public void setInvalidateSession(boolean invalidateSession) {
		this.invalidateSession = invalidateSession;
	}
	
	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		if (invalidateSession) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
		}
		else {
			memberBinder.bind(null, request);
		}
		
		String url = ServletRequestUtils.getStringParameter(request, 
				SUCCESS_URL_PARAM, defaultSucessUrl);

		if (url != null) {
			return new ModelAndView(new RedirectView(url));
		}
		else {
			return null;
		}
	}
}
