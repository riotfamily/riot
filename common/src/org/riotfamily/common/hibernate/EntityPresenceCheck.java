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

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class EntityPresenceCheck implements FactoryBean<Object> {

	private SessionFactory sessionFactory;
	
	private Class<?> entityClass;
	
	private boolean invert;
	
	public EntityPresenceCheck(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	@Transactional
	public Object getObject() throws Exception {
		Assert.notNull(entityClass, "The entityClass must be specified");
		Number count = (Number) sessionFactory.getCurrentSession()
			.createCriteria(entityClass)
			.setProjection(Projections.rowCount())
			.uniqueResult();
	
		return invert ^ (count != null && count.intValue() > 0);
	}

	public Class<?> getObjectType() {
		return Boolean.class;
	}

	public boolean isSingleton() {
		return true;
	}

	
}
