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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.property;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

public class HibernatePropertyProcessor extends PropertyProcessorAdapter 
		implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;
	
	private SessionFactory sessionFactory;
	
	private Class entityClass;
	
	public HibernatePropertyProcessor() {
	}

	public HibernatePropertyProcessor(Class entityClass,
			SessionFactory sessionFactory) {
		
		this.entityClass = entityClass;
		this.sessionFactory = sessionFactory;
	}

	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

	protected Class getEntityClass() {
		return this.entityClass;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (sessionFactory == null) {
			sessionFactory = (SessionFactory) applicationContext.getBean(
					"sessionFactory", SessionFactory.class);
		}
		Assert.notNull(entityClass, "The property 'entityClass' must be set.");
	}
	
	public String convertToString(Object object) {
		if (object == null) {
			return null;
		}
		return HibernateUtils.getIdAsString(sessionFactory, object);
	}
	
	public Object resolveString(String s) {
		if (s == null) {
			return null;
		}
		Serializable sid = HibernateUtils.convertId(entityClass, s, 
				sessionFactory);
				
		return new HibernateTemplate(sessionFactory).get(entityClass, sid);
	}
	
	public String getCacheTag(String s) {
		if (s == null) {
			return null;
		}
		return entityClass.getName() + '#' + s;
	}
	
}
