package org.riotfamily.pages.page.support;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.PageMap;

public final class PageUtils {

	private static final String PAGE_ATTRIBUTE = 
			PageUtils.class.getName() + "page";
	
	private static final String PAGE_MAP_ATTRIBUTE = 
			PageUtils.class.getName() + ".pageMap";
	
	private PageUtils() {
	}

	public static void exposePage(HttpServletRequest request, Page page) {
		request.setAttribute(PAGE_ATTRIBUTE, page);
	}
	
	public static void exposePageMap(HttpServletRequest request, 
			PageMap pageMap) {
		
		request.setAttribute(PAGE_MAP_ATTRIBUTE, pageMap);
	}
	
	public static Page getPage(HttpServletRequest request) {
		return (Page) request.getAttribute(PAGE_ATTRIBUTE);
	}
	
	public static PageMap getPageMap(HttpServletRequest request) {
		return (PageMap) request.getAttribute(PAGE_MAP_ATTRIBUTE);
	}
	
	public static Collection getRootPages(HttpServletRequest request) {
		PageMap pageMap = getPageMap(request);
		return pageMap.getRootPages();
	}
	
	public static Page getRootPage(Page page) {
		while (page.getParent() != null) {
			page = page.getParent();
		}
		return page;
	}
	
	public static Page getFirstChild(Page page) {
		Collection childPages = page.getChildPages();
		if (childPages == null || childPages.isEmpty()) {
			return null;
		}
		return (Page) childPages.iterator().next();
	}
	
	public static Page getChild(Page page, String pathComponent) {
		return getPage(page.getChildPages(), pathComponent);
	}
	
	public static Page getPage(Collection pages, String pathComponent) {
		if (pages != null) {
			Iterator it = pages.iterator();
			while (it.hasNext()) {
				Page child = (Page) it.next();
				if (child.getPathComponent().equals(pathComponent)) {
					return child;
				}
			}
		}
		return null;
	}

}
