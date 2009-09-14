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
package org.riotfamily.pages.config;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.AbstractSetupBean;
import org.riotfamily.pages.model.Site;

public class DefaultSiteCreator extends AbstractSetupBean {

	public DefaultSiteCreator(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected void setup(Session session) throws Exception {
		Site site  = Site.loadDefaultSite();
		if (site == null) {
			site = new Site();
			site.setLocale(Locale.getDefault());
			site.setName("default");
			site.save();
		}
	}
	
}
