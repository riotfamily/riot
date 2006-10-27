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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.support;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

public class HibernatePropertyEditor extends PropertyEditorSupport {

	private SessionFactory sessionFactory;
	
	private Class entityClass;
	
	public HibernatePropertyEditor(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

	public String getAsText() {
		Object entity = getValue();
		if (entity == null) {
			return null;
		}
		return HibernateUtils.getIdAsString(sessionFactory, entity);
	}
	
	public void setAsText(String id) throws IllegalArgumentException {
		if (id == null) {
			setValue(null);
		}
		Assert.notNull(entityClass, "An entityClass must be set");
		
		Serializable sid = HibernateUtils.convertId(
				entityClass, id, sessionFactory);
				
		Object entity = new HibernateTemplate(sessionFactory).get(
				entityClass, sid);
	
		setValue(entity);
	}

}
