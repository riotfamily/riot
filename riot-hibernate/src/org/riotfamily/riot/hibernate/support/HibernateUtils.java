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

import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.util.PropertyUtils;

public final class HibernateUtils {
	
	private HibernateUtils() {
	}
	
	public static Object get(Session session, Class beanClass, String id) {
		SessionFactory sf = session.getSessionFactory();
		Serializable serialId = convertId(beanClass, id, sf);
		return session.get(beanClass, serialId);
	}
	
	public static Serializable convertId(Class beanClass, String id, 
			SessionFactory sessionFactory) {
		
		Class identifierClass = sessionFactory.getClassMetadata(beanClass)
				.getIdentifierType().getReturnedClass();
		
		return (Serializable) PropertyUtils.convert(id, identifierClass);
	}

	public static String getIdAsString(SessionFactory sessionFactory, Object bean) {
		Class clazz = Hibernate.getClass(bean);
		ClassMetadata metadata = sessionFactory.getClassMetadata(clazz);
		return metadata.getIdentifier(bean, EntityMode.POJO).toString();
	}
}
