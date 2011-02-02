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
package org.riotfamily.common.hibernate;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class AbstractConditionalSetupBean extends AbstractSetupBean {

	private String condition;

	public AbstractConditionalSetupBean(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}

	@Override
	protected final void setup(Session session) throws Exception {
		if (isSetupRequired(session)) {
			doSetup(session);
		}
	}
	
	protected abstract void doSetup(Session session) throws Exception;

	private boolean isSetupRequired(Session session) {
		if (condition == null) {
			return true;
		}
		Query query = session.createQuery(condition).setMaxResults(1);
		Object test = query.uniqueResult();
		if (test instanceof Number) {
			return ((Number) test).intValue() == 0;
		}
		if (test instanceof Boolean) {
			return ((Boolean) test).booleanValue();
		}
		return test == null;
	}
	
}
