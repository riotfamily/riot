package org.riotfamily.pages.component.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;

/**
 * Default ComponentDAO implementation that uses Hibernate. All mappings
 * a specified in <code>component.hbm.xml</code> which can be found in the
 * same package.
 */
public class HibernateComponentDao extends AbstractComponentDao {

	private SessionFactory sessionFactory;
	
	public HibernateComponentDao(ComponentRepository componentRepository,
			SessionFactory sessionFactory) {

		super(componentRepository);
		this.sessionFactory = sessionFactory;
	}

	public ComponentList findComponentList(
			final String path, final String key) {
		
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from ComponentList list where list.path = :path" +
				" and list.key = :key");
				
		query.setParameter("path", path);
		query.setParameter("key", key);
		query.setMaxResults(1);
		return (ComponentList) query.uniqueResult();
	}
	
	public List findComponentLists(final String path) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"from ComponentList list where list.path = :path");
				
		query.setParameter("path", path);
		return query.list();
	}

	protected Object loadObject(Class clazz, Long id) {
		return sessionFactory.getCurrentSession().get(clazz, id);
	}
	
	protected void saveObject(Object object) {
		sessionFactory.getCurrentSession().save(object);
	}
	
	protected void updateObject(Object object) {
		sessionFactory.getCurrentSession().update(object);
	}
	
	protected void deleteObject(Object object) {
		sessionFactory.getCurrentSession().delete(object);
	}

	public void updatePaths(final String oldPath, final String newPath) {
		Query query = sessionFactory.getCurrentSession().createQuery(
				"update ComponentList set path = :newPath" +
				" where path = :oldPath");
				
		query.setParameter("oldPath", oldPath);
		query.setParameter("newPath", newPath);
		query.executeUpdate();
	}
}
