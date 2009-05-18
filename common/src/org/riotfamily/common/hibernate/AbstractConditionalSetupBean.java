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
	
	protected abstract void doSetup(Session session);

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
