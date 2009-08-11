package org.riotfamily.riot.hibernate.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class AbstractHibernateRiotDao extends HibernateDaoSupport implements RiotDao {

	public AbstractHibernateRiotDao(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public String getObjectId(Object entity) {
		return HibernateUtils.getIdAsString(getSessionFactory(), entity);
	}

	public Object load(String id) throws DataAccessException {
		return HibernateUtils.get(getSession(), getEntityClass(), id);
	}

	public int getListSize(Object parent, ListParams params) throws DataAccessException {
		return -1;
	}
	
	public final Collection<?> list(Object parent, ListParams params) {
        return listInternal(parent, params);
	}
	
	protected List<?> listInternal(Object parent, ListParams params) throws DataAccessException {
		return getSession().createCriteria(getEntityClass()).list();
	}
	
	public void save(Object entity, Object parent) throws DataAccessException {
		getSession().save(entity);
	}

	public Object update(Object entity) throws DataAccessException {
		return getSession().merge(entity);
	}
	
	public void delete(Object entity, Object parent) throws DataAccessException {
		getSession().delete(entity);
	}
	
}
