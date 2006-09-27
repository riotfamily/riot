package org.riotfamily.pages.page.support;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.controller.RedirectController;
import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.WebsiteMember;
import org.riotfamily.pages.page.Page;

/**
 * Controller that sends a redirect to the first accessible child-page. If no
 * such page exists, a 404 (not found) error is sent to the client.
 */
public class FolderController extends RedirectController {

	private Page folder;
	
	private MemberBinder memberBinder;
	
	public FolderController(Page folder, MemberBinder memberBinder) {
		this.folder = folder;
		this.memberBinder = memberBinder;
		setAddContextPath(true);
		setAddServletMapping(true);
	}

	protected String getDestination(HttpServletRequest request) {
		Collection pages = folder.getChildPages();
		if (pages != null) {
			WebsiteMember member = memberBinder.getMember(request);
			Iterator it = pages.iterator();
			while (it.hasNext()) {
				Page page = (Page) it.next();
				if (page.isAccessible(request, member)) {
					return page.getPath();
				}
			}
		}
		return null;
	}
	
}
