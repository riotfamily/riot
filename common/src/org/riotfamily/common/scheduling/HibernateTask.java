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
package org.riotfamily.common.scheduling;

import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.HibernateCallbackWithoutResult;
import org.riotfamily.common.hibernate.ThreadBoundHibernateTemplate;
import org.springframework.core.Ordered;

/**
 * Scheduled task that executes code within a Hibernate session.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public abstract class HibernateTask extends HibernateCallbackWithoutResult 
		implements ScheduledTask, Ordered {

	private String[] triggerNames;
	
	private int order = Ordered.LOWEST_PRECEDENCE;
	
	private SessionFactory sessionFactory;
	
	private ThreadBoundHibernateTemplate hibernateTemplate;
	
	public HibernateTask(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.hibernateTemplate = new ThreadBoundHibernateTemplate(sessionFactory);
	}
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public String[] getTriggerNames() {
		return triggerNames;
	}
	
	public void setTriggerNames(String[] triggerNames) {
		this.triggerNames = triggerNames;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	public void execute() throws Exception {
		hibernateTemplate.execute(this);
	}
		
}
