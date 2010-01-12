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

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.AbstractSetupBean;
import org.riotfamily.pages.model.Site;

/*
 * Synchronizes persitent system pages with
 * those defined in setup.xml during application 
 * startup
 * 
 */

public class SystemPageSynchronizer extends AbstractSetupBean {

	public SystemPageSynchronizer(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected void setup(Session session) throws Exception {
		List<Site> sites  = Site.findAll();
		for (Site site : sites) {
			site.getSchema().syncSystemPages(site);
		}
	}
	
}
