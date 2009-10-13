/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.pages.riot.security;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.policy.AuthorizationPolicy;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

public class SiteUserPolicy implements AuthorizationPolicy {
	
	private PageResolver pageResolver;
	
	private int order = Integer.MAX_VALUE - 2;
	
	public SiteUserPolicy(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}
	
    public int getOrder() {
		return this.order;
	}

    public void setOrder(int order) {
		this.order = order;
	}
	
	public Permission getPermission(RiotUser riotUser, String action, Object object) {
		if (riotUser instanceof SiteUser) {
			SiteUser user = (SiteUser) riotUser;
			
			if (isLimited(user)) {
				boolean denied = false;
				if (object != null && object.getClass().isArray()) {
					Object[] objects = (Object[]) object;
					for (Object o : objects) {
						denied |= isDenied(user, o);
					}
				}
				else {
					denied |= isDenied(user, object);
				}
				if (denied) {
					return Permission.DENIED;
				}
			}
		}
		return Permission.ABSTAIN;
	}
		
	private boolean isDenied(SiteUser user, Object object) {
		if (object instanceof Site) {
			Site site = (Site) object;
			return !user.getSites().contains(site);
		}
		if (object instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) object;
			Page page = pageResolver.getPage(request);
			return page != null && !user.getSites().contains(page.getSite());
		}
		return false;
	}

	protected boolean isLimited(SiteUser siteUser) {
		Set<Site> sites = siteUser.getSites();
		if (sites != null && sites.size() > 0) {
			return true;
		}
		return false;
	}
	
}
