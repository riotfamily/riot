package org.riotfamily.pages.member.support;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.WebsiteMember;

public class NullMemberBinder implements MemberBinder {

	public void bind(WebsiteMember member, HttpServletRequest request) {
		throw new UnsupportedOperationException("This implementation does not "
				+ "support binding a member to the request.");
	}

	public WebsiteMember getMember(HttpServletRequest request) {
		return null;
	}
	
}
