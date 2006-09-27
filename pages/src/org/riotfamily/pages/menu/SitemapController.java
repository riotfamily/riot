package org.riotfamily.pages.menu;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.MemberBinderAware;
import org.riotfamily.pages.member.WebsiteMember;
import org.riotfamily.pages.member.support.NullMemberBinder;
import org.riotfamily.pages.mvc.cache.AbstractCachingPolicyController;
import org.springframework.web.servlet.ModelAndView;

public class SitemapController extends AbstractCachingPolicyController implements MemberBinderAware {

	private SitemapBuilder sitemapBuilder;
	
	private String viewName;
	
	private ServletMappingHelper servletMappingHelper;
	
	private String contextPath;
	
	private String servletPrefix;
	
	private String servletSuffix;

	private boolean includeMemberRoleInCacheKey = true;
	
	private MemberBinder memberBinder = new NullMemberBinder();	

	public SitemapController() {
		servletMappingHelper = new ServletMappingHelper();
		servletMappingHelper.setUseOriginalRequest(true);
	}
	
	public void setSitemapBuilder(SitemapBuilder sitemapBuilder) {
		this.sitemapBuilder = sitemapBuilder;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		List items = sitemapBuilder.buildSitemap(request);
		completeLinks(items, request, response);
		return new ModelAndView(viewName, "items", items);
	}
		
	protected void completeLinks(Collection items, HttpServletRequest request, 
			HttpServletResponse response) {
		
		if (items == null) {
			return;
		}
		Iterator it = items.iterator();
		while (it.hasNext()) {
			MenuItem item = (MenuItem) it.next();
			StringBuffer link = new StringBuffer();
			link.append(getContextPath(request));
			link.append(getServletPrefix(request));
			link.append(item.getLink());
			link.append(getServletSuffix(request));
			item.setLink(response.encodeURL(link.toString()));
			completeLinks(item.getChildItems(), request, response);
		}
	}

	protected String getContextPath(HttpServletRequest request) {
		if (contextPath == null) {
			contextPath = servletMappingHelper.getContextPath(request);
		}
		return contextPath;
	}
	
	protected String getServletPrefix(HttpServletRequest request) {
		if (servletPrefix == null) {
			servletPrefix = servletMappingHelper.getServletPrefix(request);
		}
		return servletPrefix;
	}
	
	protected String getServletSuffix(HttpServletRequest request) {
		if (servletSuffix == null) {
			servletSuffix = servletMappingHelper.getServletSuffix(request);
		}
		return servletSuffix;
	}
	
	public void appendCacheKeyInternal(StringBuffer key, 
			HttpServletRequest request) {
		
		super.appendCacheKeyInternal(key, request);		
		if (includeMemberRoleInCacheKey) {
			WebsiteMember member = memberBinder.getMember(request);
			if (member != null) {
				key.append("#role=");
				key.append(member.getRole());
			}
		}
	}
	
	public long getLastModified(HttpServletRequest request) {
		return sitemapBuilder.getLastModified(request);
	}

}
