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
package org.riotfamily.core.status;

import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.ScreenContext;
import org.springframework.beans.factory.annotation.Required;

public class HqlStatusMonitor extends I18nStatusMonitor {	
	
	private SessionFactory sessionFactory;
	
	private String hql;
	
	public HqlStatusMonitor(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Required
	public void setHql(String hql) {
		this.hql = hql;
	}

	protected Object[] getArgs(ScreenContext context) {	
		Object result = sessionFactory.getCurrentSession()
				.createQuery(hql)
				.uniqueResult();
		
		if (result == null) {
			return null;
		}
		else if (result instanceof Object[]) {
			return (Object[]) result;			
		}
		else {
			return new Object[] { result };
		}
	}

}
