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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.HibernateUtils;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Order;
import org.riotfamily.core.dao.Searchable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * RiotDao implementation based on Hibernate.
 */
public abstract class AbstractHqlDao extends AbstractHibernateRiotDao 
		implements Searchable {

	private Logger log = LoggerFactory.getLogger(AbstractHqlDao.class);

	private String[] searchableProperties;
	
    public AbstractHqlDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	protected boolean isPolymorph() {
        return true;
    }

    protected String getSelect() {
    	return "this";
    }

    protected String getFrom(ListParams params) {
    	StringBuilder from = new StringBuilder();
    	from.append(getEntityClass().getName());
    	from.append(" as this");
    	return from.toString();
    }
    
    public void setSearch(String search) {
		searchableProperties = StringUtils.tokenizeToStringArray(search, " ,\t\r\n");
	}
    
    public String[] getSearchableProperties() {
		return searchableProperties;
	}

	protected String getWhere() {
    	return null;
    }
    
    protected boolean isPrefixSearch() {
    	return true;
    }
    
    protected boolean isSuffixSearch() {
    	return true;
    }
    
	/**
     * Returns a list of items.
     */
	@Override
    protected List<?> listInternal(Object parent, ListParams params) {
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
    @Override
	public int getListSize(Object parent, ListParams params) {
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
    		String search = params.getSearch().toLowerCase();
    		if (isPrefixSearch()) {
    			search += '%';
    		}
    		if (isSuffixSearch()) {
    			search = '%' + search;
    		}
    		if (!isPrefixSearch() && !isSuffixSearch()) {
    			search = search.replace('*', '%');
    		}
    		query.setParameter("search", search);
    	}
    }

    /**
     * Builds a HQL query string to retrieve the total number of items.
     */
    protected String buildCountHql(Object parent, ListParams params) {
    	StringBuilder hql = new StringBuilder();
    	hql.append("select count(*)");
    	appendFromClause(hql, params);
    	HqlUtils.appendHql(hql, "where", getWhereClause(parent, params));
    	log.debug(hql.toString());
        return hql.toString();
    }

    /**
     * Builds a HQL query string to retrieve a list of items.
     */
    protected String buildHql(Object parent, ListParams params) {
    	StringBuilder hql = new StringBuilder();
    	hql.append("select ");
    	hql.append(getSelect());
    	appendFromClause(hql, params);
    	HqlUtils.appendHql(hql, "where", getWhereClause(parent, params));
    	HqlUtils.appendHql(hql, "order by", getOrderBy(params));
    	log.debug(hql.toString());
        return hql.toString();
    }

	protected void appendFromClause(StringBuilder hql, ListParams params) {
		hql.append(" from ");
    	hql.append(getFrom(params));
	}

    protected String getWhereClause(Object parent, ListParams params) {
        StringBuilder sb = new StringBuilder();
        HqlUtils.appendHql(sb, null, getWhere());

    	if (params.getFilter() != null) {
    		HqlUtils.appendHql(sb, "and", getFilterWhereClause(params));
    	}

    	if (params.getSearch() != null) {
    		HqlUtils.appendHql(sb, "and", getSearchWhereClause(params));
        }

        if (!isPolymorph()) {
        	HqlUtils.appendHql(sb, "and", "(this.class = ")
        		.append(getEntityClass().getName()).append(')');
        }

        return sb.toString();
    }
    
    protected String getFilterWhereClause(ListParams params) {
    	return HqlUtils.getExampleWhereClause(getEntityClass(),
    				params.getFilter(), "this",	params.getFilteredProperties());
    }
    
    @SuppressWarnings("unchecked")
	protected void setFilterParameters(Query query, ListParams params) {
    	if (params.getFilter() instanceof Map) {
			Map<String, ?> filterMap = (Map<String, ?>) params.getFilter();
			for (Map.Entry<String, ?> entry: filterMap.entrySet()) {
				String name = entry.getKey();
				HibernateUtils.setParameter(query, name.replaceAll("\\.", "_dot_"), entry.getValue());
			}
		}
		else {
			query.setProperties(params.getFilter());
		}

		HqlUtils.setCollectionValueParams(query,
				params.getFilteredProperties(), getEntityClass(), 
				params.getFilter());
    }
    
    protected String getSearchWhereClause(ListParams params) {
    	return HqlUtils.getSearchWhereClause(getEntityClass().getName(), "this", "search", getSearchableProperties());
    }
    
    protected String getOrderBy(ListParams params) {
        StringBuilder sb = new StringBuilder();
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