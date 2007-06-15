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
package org.riotfamily.components.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.components.ComponentList;
import org.riotfamily.components.Location;
import org.riotfamily.components.VersionContainer;
import org.riotfamily.components.dao.AbstractComponentDao;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.util.Assert;

/**
 * Default ComponentDAO implementation that uses Hibernate. All mappings
 * a specified in <code>component.hbm.xml</code> which can be found in the
 * same package.
 */
public class HibernateComponentDao extends AbstractComponentDao {

	private HibernateHelper hibernate;

	public HibernateComponentDao() {
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernate = new HibernateHelper(sessionFactory, "components");
	}

	protected void initDao() {
		Assert.notNull(hibernate, "A SessionFactory must be set.");
	}

	public ComponentList findComponentList(Location location) {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName()	+ " list where "
				+ "list.location = :location and list.parent is null");

		hibernate.setParameter(query, "location", location);
		return (ComponentList) hibernate.uniqueResult(query);
	}

	public ComponentList findComponentList(VersionContainer parent, String slot) {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName() + " list where list.parent = "
				+":parent and list.location.slot = :slot");

		hibernate.setParameter(query, "parent", parent);
		hibernate.setParameter(query, "slot", slot);
		return (ComponentList) query.uniqueResult();
	}

	public List findComponentLists(String type, String path) {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName() + " list where "
				+ "list.location.type = :type "
				+ "and list.location.path = :path "
				+ "and list.parent is null");

		hibernate.setParameter(query, "type", type);
		hibernate.setParameter(query, "path", path);
		return hibernate.list(query);
	}

	public List findDirtyComponentLists() {
		Query query = hibernate.createCacheableQuery("from "
				+ ComponentList.class.getName()
				+ " list where list.dirty = true");

		return hibernate.list(query);
	}

	protected Object loadObject(Class clazz, Long id) {
		return hibernate.load(clazz, id);
	}

	protected void saveObject(Object object) {
		hibernate.save(object);
	}

	protected void updateObject(Object object) {
		hibernate.update(object);
		hibernate.flush();
	}

	protected void deleteObject(Object object) {
		hibernate.delete(object);
	}

}
