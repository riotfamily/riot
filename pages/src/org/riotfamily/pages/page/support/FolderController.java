/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
