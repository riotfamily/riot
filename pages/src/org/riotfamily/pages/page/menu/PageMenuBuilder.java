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
import org.riotfamily.pages.page.support.PageUtils;
import org.springframework.util.StringUtils;

public class PageMenuBuilder implements MenuBuilder, MemberBinderAware {

	private MemberBinder memberBinder = new NullMemberBinder();
	
	private ViewModeResolver viewModeResolver;
	
	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}
	
	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	public long getLastModified(HttpServletRequest request) {
		return PageUtils.getPageMap(request).getLastModified();
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
			siblings = PageUtils.getRootPages(request);
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
				MenuItem item = new MenuItem();
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
