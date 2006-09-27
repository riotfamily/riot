package org.riotfamily.pages.member;

import javax.servlet.http.HttpServletRequest;

public interface MemberBinder {

	public void bind(WebsiteMember member, HttpServletRequest request);
	
	public WebsiteMember getMember(HttpServletRequest request);

}
