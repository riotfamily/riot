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
package org.riotfamily.components.index;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.web.mvc.interceptor.RequestInterceptorAdapter;
import org.riotfamily.components.support.EditModeUtils;

/**
 * RequestInterceptor that enables the <code>contentIndex</code> 
 * Hibernate filter.
 */
public class ContentFilterRequestInterceptor extends RequestInterceptorAdapter {

	private SessionFactory sessionFactory;
	
	public ContentFilterRequestInterceptor(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Session session = sessionFactory.getCurrentSession();
		if (session != null) {
			boolean live = !EditModeUtils.isPreview(request, null);
			session.enableFilter("contentIndex").setParameter("live", live);
			if (live) {
				session.enableFilter("publishedContent");
			} 
		}
		return true;
	}

}
