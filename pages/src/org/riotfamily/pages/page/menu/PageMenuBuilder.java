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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.page.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.component.preview.DefaultViewModeResolver;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.MemberBinderAware;
import org.riotfamily.pages.member.WebsiteMember;
import org.riotfamily.pages.member.support.NullMemberBinder;
import org.riotfamily.pages.menu.MenuBuilder;
import org.riotfamily.pages.menu.MenuItem;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;
import org.riotfamily.pages.page.support.PageUtils;
import org.springframework.util.StringUtils;

public class PageMenuBuilder implements MenuBuilder, MemberBinderAware {

	private MemberBinder memberBinder = new NullMemberBinder();
	
	private ViewModeResolver viewModeResolver;
	
	private PageMap pageMap;
	
	public PageMenuBuilder(PageMap pageMap) {
		this.pageMap = pageMap;
	}
	
	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}
	
	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	public long getLastModified(HttpServletRequest request) {
		return pageMap.getLastModified();
	}
	
	public List buildMenu(HttpServletRequest request) {
		Page page = PageUtils.getPage(request);
		return buildMenu(request, page, true, createChildItems(page, request));
	}
		
	protected List buildMenu(HttpServletRequest request, 
			Page expandedPage, boolean active, List childItems) {
		
		Page parent = null;
		if (expandedPage != null) {
			parent = expandedPage.getParent();
		}
		
		Collection siblings;
		if (parent != null) {
			siblings = parent.getChildPages();
		}
		else {
			siblings = pageMap.getRootPages();
		}

		List items = null;
		if (siblings != null) {
			items = createItems(siblings, expandedPage, active, childItems, request);
		}
		
		if (parent != null) {
			return buildMenu(request, parent, false, items);
		}
		return items;
	}
	
	protected List createChildItems(Page page, HttpServletRequest request) {
		if (page == null || page.getChildPages() == null) {
			return null;
		}
		return createItems(page.getChildPages(), null, false, null, request);
	}
	
	protected List createItems(Collection pages, Page expandedPage, 
			boolean active, List childItems, HttpServletRequest request) {
		if (viewModeResolver == null) {
			viewModeResolver = new DefaultViewModeResolver();
		}
		WebsiteMember member = memberBinder.getMember(request);
		ArrayList items = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (!page.isHidden() && page.isAccessible(request, member) && (page.isPublished() || viewModeResolver.isPreviewMode(request))) {
				MenuItem item = new MenuItem(page);
				item.setLabel(page.getTitle());
				item.setLink(page.getPath());
				item.setStyle(StringUtils.getFilename(page.getPath()));
				items.add(item);
				if (page.equals(expandedPage)) {
					item.setExpanded(true);
					item.setActive(active);
					item.setChildItems(childItems);
				}
			}
		}
		
		if (expandedPage != null && !items.isEmpty()) {
			Page parent = expandedPage.getParent();
			if (parent != null && parent.isFolder()) {
				MenuItem firstItem = (MenuItem) items.get(0);
				firstItem.setLink(parent.getPath());
			}
		}
		
		return items;
	}

}
