/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.dao.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.HibernateUtils;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.dao.Sortable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DaoSupport;

public abstract class AbstractHibernateRiotDao extends DaoSupport implements RiotDao, Sortable {

	private SessionFactory sessionFactory;
	
	public AbstractHibernateRiotDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	protected Session getSession() {
		return sessionFactory.getCurrentSession();
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
	
    public boolean canSortBy(String property) {
    	return HibernateUtils.isPersistentProperty(
    			getSessionFactory(), getEntityClass(), property);
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
	
	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		if (this.sessionFactory == null) {
			throw new IllegalArgumentException("'sessionFactory' is required");
		}
	}
	
}
