package org.riotfamily.pages.page.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.component.preview.DefaultViewModeResolver;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.MemberBinderAware;
import org.riotfamily.pages.member.WebsiteMember;
import org.riotfamily.pages.member.support.NullMemberBinder;
import org.riotfamily.pages.menu.MenuItem;
import org.riotfamily.pages.menu.SitemapBuilder;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;

public class PageSitemapBuilder implements SitemapBuilder, MemberBinderAware {

	private int rootLevel = 0;
	
	private ViewModeResolver viewModeResolver;
	
	private MemberBinder memberBinder = new NullMemberBinder();
	
	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}
	
	public void setRootLevel(int level) {
		this.rootLevel = level;
	}

	public void setViewModeResolver(ViewModeResolver viewModeResolver) {
		this.viewModeResolver = viewModeResolver;
	}

	public List buildSitemap(HttpServletRequest request) {
		Collection rootPages = null;
		if (rootLevel > 0) {
			Page page = PageUtils.getPage(request);
			LinkedList path = new LinkedList();
			while (page != null) {
				path.add(0, page);
				page = page.getParent();
			}
			Page parent = (Page) path.get(rootLevel - 1);
			rootPages = parent.getChildPages();
		}
		else {
			rootPages = PageUtils.getRootPages(request);
		}
		return createItems(rootPages, request);
	}
		
	public List createItems(Collection pages, HttpServletRequest request) {
		if (viewModeResolver == null) {
			viewModeResolver = new DefaultViewModeResolver();
		}
		ArrayList items = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (includePage(page, request)) {
				MenuItem item = new MenuItem();
				item.setLabel(page.getTitle());
				item.setLink(page.getPath());
				items.add(item);
				Collection childPages = page.getChildPages();
				if (childPages != null && !childPages.isEmpty()) {
					item.setChildItems(createItems(page.getChildPages(), request));
				}
			}
		}
		return items;
	}
	
	protected boolean includePage(Page page, HttpServletRequest request) {
		WebsiteMember member = memberBinder.getMember(request);
		return !page.isHidden() && page.isAccessible(request, member) && (page.isPublished() || viewModeResolver.isPreviewMode(request));		
	}
	
	public long getLastModified(HttpServletRequest request) {
		return PageUtils.getPageMap(request).getLastModified();
	}

}
