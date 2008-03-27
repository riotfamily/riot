package org.riotfamily.pages.view;

import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.model.Site;

public class SiteFacade {
	
	private Site site;

	private HttpServletRequest request;
	
	public SiteFacade(Site site, HttpServletRequest request) {
		this.site = site;
		this.request = request;
	}
	
	public Site getSite() {
		return site;
	}
	
	public String getAbsoluteUrl() {
		return makeAbsolute("");
	}
	
	public String makeAbsolute(String path) {
		return site.makeAbsolute(request.isSecure(),
				ServletUtils.getServerNameAndPort(request), 
				request.getContextPath(), path);
	}

	public Set getAliases() {
		return site.getAliases();
	}

	public Set getDerivedSites() {
		return site.getDerivedSites();
	}

	public String getHostName() {
		return site.getHostName();
	}

	public Long getId() {
		return site.getId();
	}

	public Locale getLocale() {
		return site.getLocale();
	}

	public Site getMasterSite() {
		return site.getMasterSite();
	}

	public String getName() {
		return site.getName();
	}

	public String getPathPrefix() {
		return site.getPathPrefix();
	}

	public String getTheme() {
		return site.getTheme();
	}

	public boolean isEnabled() {
		return site.isEnabled();
	}

	public String toString() {
		return site.toString();
	}
	
}
