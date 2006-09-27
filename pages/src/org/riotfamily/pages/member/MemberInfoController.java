package org.riotfamily.pages.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class MemberInfoController implements Controller, MemberBinderAware {

	public static final String MODEL_KEY = "member";
	
	private String memberViewName;
	
	private String publicViewName;
	
	private MemberBinder memberBinder;
	
	public void setMemberViewName(String viewName) {
		this.memberViewName = viewName;
	}

	public void setPublicViewName(String publicViewName) {
		this.publicViewName = publicViewName;
	}
	
	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		WebsiteMember member = memberBinder.getMember(request);
		if (member != null && memberViewName != null) {
			return new ModelAndView(memberViewName, MODEL_KEY, member);
		}
		else if (member == null && publicViewName != null) {
			return new ModelAndView(publicViewName);
		}
		else {
			return null;
		}
	}

	

}
