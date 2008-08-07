/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.support;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class EntityPresenceCheck implements FactoryBean {

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
