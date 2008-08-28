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
package org.riotfamily.riot.hibernate.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.dao.SortableDao;
import org.riotfamily.riot.hibernate.support.HibernateUtils;

/**
 * RiotDao implementation based on Hibernate.
 */
public abstract class AbstractHqlDao extends AbstractHibernateRiotDao 
		implements SortableDao {

	private Log log = LogFactory.getLog(AbstractHqlDao.class);

    public AbstractHqlDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	protected boolean isPolymorph() {
        return true;
    }

    protected String getSelect() {
    	return "this";
    }

    protected String getFrom() {
    	return getEntityClass().getName() + " as this";
    }
    
    protected String getWhere() {
    	return null;
    }
    
	/**
     * Returns a list of items.
     */
	@Override
    protected final List<?> listInternal(Object parent, ListParams params) {
    	Query query = getSession().createQuery(buildHql(parent, params));
    	setQueryParameters(query, parent, params);
        if (params.getPageSize() > 0) {
            query.setFirstResult(params.getOffset());
            query.setMaxResults(params.getPageSize());
        }
        return query.list();
    }

    /**
     * Returns the total number of items.
     */
    public final int getListSize(Object parent, ListParams params) {
        Query query = getSession().createQuery(buildCountHql(parent, params));
        setQueryParameters(query, parent, params);
        Number size = (Number) query.uniqueResult();
        if (size == null) {
        	return 0;
        }
        return size.intValue();
    }

    protected void setQueryParameters(Query query, Object parent,
    		ListParams params) {

    	if (params.getFilter() != null) {
    		setFilterParameters(query, params);
        }
    	if (params.getSearch() != null) {
    		query.setParameter("search", params.getSearch()
    				.toLowerCase().replace('*', '%') + "%");
    	}
    }

    /**
     * Builds a HQL query string to retrieve the total number of items.
     */
    protected final String buildCountHql(Object parent, ListParams params) {
    	StringBuffer hql = new StringBuffer();
    	hql.append("select count(*) from ");
    	hql.append(getFrom());
    	HibernateUtils.appendHql(hql, "where", getWhereClause(parent, params));
    	log.debug(hql);
        return hql.toString();
    }

    /**
     * Builds a HQL query string to retrieve a list of items.
     */
    protected final String buildHql(Object parent, ListParams params) {
    	StringBuffer hql = new StringBuffer();
    	hql.append("select ");
    	hql.append(getSelect());
    	hql.append(" from ");
    	hql.append(getFrom());
    	HibernateUtils.appendHql(hql, "where", getWhereClause(parent, params));
    	HibernateUtils.appendHql(hql, "order by", getOrderBy(params));
    	log.debug(hql);
        return hql.toString();
    }

    protected String getWhereClause(Object parent, ListParams params) {
        StringBuffer sb = new StringBuffer();
        HibernateUtils.appendHql(sb, null, getWhere());

    	if (params.getFilter() != null) {
    		HibernateUtils.appendHql(sb, "and", getFilterWhereClause(params));
    	}

    	if (params.getSearch() != null) {
    		HibernateUtils.appendHql(sb, "and", getSearchWhereClause(params));
        }

        if (!isPolymorph()) {
        	HibernateUtils.appendHql(sb, "and", "(this.class = ")
        		.append(getEntityClass().getName()).append(')');
        }

        return sb.toString();
    }
    
    protected String getFilterWhereClause(ListParams params) {
    	return HibernateUtils.getExampleWhereClause(getEntityClass(),
    				params.getFilter(), "this",	params.getFilteredProperties());
    }
    
    protected void setFilterParameters(Query query, ListParams params) {
    	if (params.getFilter() instanceof Map) {
			Map<?, ?> filterMap = (Map<?, ?>) params.getFilter();
			query.setProperties(filterMap);
		}
		else {
			query.setProperties(params.getFilter());
		}

		HibernateUtils.setCollectionValueParams(query,
				params.getFilteredProperties(), getEntityClass(), 
				params.getFilter());
    }
    
    protected String getSearchWhereClause(ListParams params) {
    	return HibernateUtils.getSearchWhereClause("this", 
    				params.getSearchProperties(), "search");
    	
    }

    protected String getOrderBy(ListParams params) {
        StringBuffer sb = new StringBuffer();
        if (params.hasOrder()) {
        	Iterator<Order> it = params.getOrder().iterator();
        	while (it.hasNext()) {
        		Order order = it.next();
        		if (!order.isCaseSensitive()) {
        			sb.append(" lower(");
        		}
        		sb.append(" this.");
        		sb.append(order.getProperty());
        		if (!order.isCaseSensitive()) {
        			sb.append(" ) ");
        		}
        		sb.append(' ');
        		sb.append(order.isAscending() ? "asc" : "desc");
        		if (it.hasNext()) {
        			sb.append(',');
        		}
        	}
        }
        return sb.toString();
    }

}