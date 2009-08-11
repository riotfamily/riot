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
package org.riotfamily.pages.mapping;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.mapping.AdvancedBeanNameHandlerMapping;
import org.riotfamily.common.servlet.RequestHolder;
import org.riotfamily.common.util.Generics;
import org.riotfamily.pages.model.Site;

/**
 * AdvancedBeanNameHandlerMapping that can fill in the path prefix of the 
 * current Site.
 * <p>
 * Lets assume you have a controller with the following mapping:
 * <pre>
 * &lt;bean id="foo" name="${sitePrefix*}/foo.html" ... &gt;
 * </pre>
 * Then writing <code>${common.urlForHandler('foo')}</code> in your .ftl will
 * result in a valid URL, prefixed with the current Site's pathPrefix.
 * <p>
 * Note: The mapping currently doesn't perform any sanity check upon lookup,
 * i.e. the pattern above will also match "/bar/baz/foo.html", even though
 * "/bar/baz" is no valid Site prefix.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteBeanNameHandlerMapping extends AdvancedBeanNameHandlerMapping {

	private PageResolver pageResolver;
	
	public SiteBeanNameHandlerMapping(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}
	
	public Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		Object handler = super.getHandlerInternal(request);
		if (handler != null) {
			pageResolver.getSite(request);
		}
		return handler;
	}

	@Override
	protected Map<String, Object> getDefaults() {
		HttpServletRequest request = RequestHolder.getRequest();
		if (request != null) {
			Map<String, Object> defaults = Generics.newHashMap();
			Site site = PageResolver.getResolvedSite(request);
			defaults.put("site", site);
			String sitePrefix = null;
			if (site != null) {
				sitePrefix = site.getPathPrefix();
			}
			if (sitePrefix == null) {
				sitePrefix = "";
			}
			defaults.put("sitePrefix", sitePrefix);
			return defaults;
		}
		return null;
	}
}
