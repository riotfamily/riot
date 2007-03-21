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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.components.ComponentList;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.Location;
import org.riotfamily.components.VersionContainer;
import org.riotfamily.components.dao.AbstractComponentDao;

/**
 * Default ComponentDAO implementation that uses Hibernate. All mappings
 * a specified in <code>component.hbm.xml</code> which can be found in the
 * same package.
 */
public class HibernateComponentDao extends AbstractComponentDao {

	private SessionFactory sessionFactory;
	
	public HibernateComponentDao(ComponentRepository repository,
			SessionFactory sessionFactory) {
		
		super(repository);
		this.sessionFactory = sessionFactory;
	}

	public ComponentList findComponentList(Location location) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from ComponentList list where list.location = :location " +
				"and list.parent is null");
				
		query.setParameter("location", location);
		query.setCacheable(true);
		query.setCacheRegion("components");
		return (ComponentList) query.uniqueResult();
	}
	
	public ComponentList findComponentList(VersionContainer parent, String slot) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from ComponentList list where list.parent = :parent " +
				"and list.location.slot = :slot");
				
		query.setParameter("parent", parent);
		query.setParameter("slot", slot);
		query.setCacheable(true);
		query.setCacheRegion("components");
		return (ComponentList) query.uniqueResult();
	}
	
	public List findComponentLists(String type, String path) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from ComponentList list where " +
				"list.location.type = :type " +
				"and list.location.path = :path " +
				"and list.parent is null");
		
		query.setParameter("type", type);
		query.setParameter("path", path);
		query.setCacheable(true);
		query.setCacheRegion("components");
		return query.list();
	}
	
	public List findDirtyComponentLists() {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from ComponentList list where list.dirty = true");
		
		query.setCacheable(true);
		query.setCacheRegion("components");
		return query.list();
	}

	protected Object loadObject(Class clazz, Long id) {
		return sessionFactory.getCurrentSession().load(clazz, id);
	}
	
	protected void saveObject(Object object) {
		sessionFactory.getCurrentSession().save(object);
	}
	
	protected void updateObject(Object object) {
		Session session = sessionFactory.getCurrentSession();
		session.update(object);
		session.flush();
	}
	
	protected void deleteObject(Object object) {
		sessionFactory.getCurrentSession().delete(object);
	}

}
