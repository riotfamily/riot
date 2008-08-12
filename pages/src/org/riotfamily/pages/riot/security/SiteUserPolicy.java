package org.riotfamily.pages.riot.security;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.security.auth.RiotUser;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;

public class SiteUserPolicy implements AuthorizationPolicy {
	
	private PageResolver pageResolver;
	
	private int order = Integer.MAX_VALUE - 2;
	
	public SiteUserPolicy(PageDao pageDao) {
		this.pageResolver = new PageResolver(pageDao);
	}
	
    public int getOrder() {
		return this.order;
	}

    public void setOrder(int order) {
		this.order = order;
	}
	
	public int checkPermission(RiotUser riotUser, String action, Object object) {
		if (riotUser instanceof SiteUser) {
			SiteUser user = (SiteUser) riotUser;
			
			if (isLimited(user)) {				
				if (object instanceof Site) {
					Site site = (Site) object;
					if (!user.getSites().contains(site)) {
						return ACCESS_DENIED;
					}
				}
				if (object instanceof HttpServletRequest) {
					HttpServletRequest request = (HttpServletRequest) object;
					Page page = pageResolver.getPage(request);
					if (page != null && !user.getSites().contains(page.getSite())) {
						return ACCESS_DENIED;
					}
				}
			}
		}
		return ACCESS_ABSTAIN;
	}

	protected boolean isLimited(SiteUser siteUser) {
		Set<Site> sites = siteUser.getSites();
		if (sites != null && sites.size() > 0) {
			return true;
		}
		return false;
	}
	
}
