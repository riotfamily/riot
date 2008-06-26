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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.dao.SortableDao;
import org.riotfamily.riot.dao.SwappableItemDao;
import org.riotfamily.riot.hibernate.support.HibernateSupport;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.riotfamily.riot.list.support.EmptyListParams;
import org.springframework.util.Assert;

/**
 * RiotDao implementation based on Hibernate.
 */
public class HqlDao extends HibernateSupport implements RiotDao,
		SortableDao, SwappableItemDao {

	private static final Log log = LogFactory.getLog(HqlDao.class);

    private Class<?> entityClass;

    private boolean polymorph = true;

    private String where;

    private String positionProperty;
    
    private boolean setPositionOnSave;

	/**
     * @return Returns the itemClass.
     */
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * @param itemClass The itemClass to set.
     */
    public void setEntityClass(Class<?> itemClass) {
        this.entityClass = itemClass;
    }

    /**
     * @return Returns the polymorph.
     */
    public boolean isPolymorph() {
        return polymorph;
    }

    /**
     * @param polymorph The polymorph to set.
     */
    public void setPolymorph(boolean polymorph) {
        this.polymorph = polymorph;
    }

    /**
     * @return Returns the where.
     */
    public String getWhere() {
        return where;
    }

    /**
     * @param where The where to set.
     */
    public void setWhere(String where) {
        this.where = where;
    }

	public void setPositionProperty(String positionProperty) {
		this.positionProperty = positionProperty;
	}

	public String getObjectId(Object item) {
		return HibernateUtils.getIdAsString(getSessionFactory(), item);
	}

	/**
     * Returns a list of items.
     */
    public Collection<?> list(Object parent, ListParams params) {
        return listInternal(parent, params);
    }

    protected List<?> listInternal(Object parent, ListParams params) {
    	Query query = createQuery(buildHql(parent, params));
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
    public int getListSize(Object parent, ListParams params) {
        Query query = createQuery(buildCountHql(parent, params));
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
    		if (params.getFilter() instanceof Map) {
    			Map filterMap = (Map) params.getFilter();
    			query.setProperties(filterMap);
    		}
    		else {
    			query.setProperties(params.getFilter());
    		}

    		HibernateUtils.setCollectionValueParams(query,
    				params.getFilteredProperties(), params.getFilter());
        }
    	if (params.getSearch() != null) {
    		query.setParameter("search", params.getSearch()
    				.toLowerCase().replace('*', '%') + "%");
    	}
    }

    /**
     * Builds a HQL query string to retrieve the maximal position property value
     */
    protected String buildMaxPositionHql(Object parent) {
    	StringBuffer hql = new StringBuffer();
    	hql.append("select max(").append(positionProperty).append(") from ");
    	hql.append(entityClass.getName());
    	hql.append(" as this");
    	HibernateUtils.appendHql(
    		hql, "where", getWhereClause(parent, new EmptyListParams()));
    	log.debug(hql);
        return hql.toString();
    }

    /**
     * Builds a HQL query string to retrieve the total number of items.
     */
    protected String buildCountHql(Object parent, ListParams params) {
    	StringBuffer hql = new StringBuffer();
    	hql.append("select count(this) from ");
    	hql.append(entityClass.getName());
    	hql.append(" as this");
    	HibernateUtils.appendHql(hql, "where", getWhereClause(parent, params));
    	log.debug(hql);
        return hql.toString();
    }

    /**
     * Builds a HQL query string to retrieve a list of items.
     */
    protected String buildHql(Object parent, ListParams params) {
    	StringBuffer hql = new StringBuffer();
    	hql.append("select this from ");
    	hql.append(entityClass.getName());
    	hql.append(" as this");
    	HibernateUtils.appendHql(hql, "where", getWhereClause(parent, params));
    	HibernateUtils.appendHql(hql, "order by", getOrderBy(params));
    	log.debug(hql);
        return hql.toString();
    }

    protected String getWhereClause(Object parent, ListParams params) {
        StringBuffer sb = new StringBuffer();
        HibernateUtils.appendHql(sb, null, where);

    	if (params.getFilter() != null) {
    		String filter = HibernateUtils.getExampleWhereClause(
    				params.getFilter(), "this",
    				params.getFilteredProperties());

    		HibernateUtils.appendHql(sb, "and", filter);
    	}

    	if (params.getSearch() != null) {
    		String search = HibernateUtils.getSearchWhereClause("this",
    				params.getSearchProperties(), "search");

    		HibernateUtils.appendHql(sb, "and", search);
        }

        if (!polymorph) {
        	HibernateUtils.appendHql(sb, "and", "(this.class = ")
        		.append(entityClass.getName()).append(')');
        }

        return sb.toString();
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
    
    protected void setPositionIfNeeded(Object entity, Object parent) {
    	if (setPositionOnSave) {
    		Query query = createQuery(buildMaxPositionHql(parent));
    		Number maxPosition = (Number) query.uniqueResult();
    		
    		PropertyUtils.setProperty(entity, positionProperty,
    			new Integer(maxPosition != null? maxPosition.intValue() + 1: 0)); 
    	}
    }

    public void save(Object entity, Object parent) {
    	setPositionIfNeeded(entity, parent);
    	getSession().save(entity);
    }

    public void delete(Object entity, Object parent) {
    	getSession().delete(entity);
    }

    public Object load(String objectId) {
    	Assert.notNull(objectId, "A non-null id must be passed to load()");
		return HibernateUtils.get(getSession(), entityClass, objectId);
    }

    public Object merge(Object entity) {
		return getSession().merge(entity);
	}
    
    public void update(Object entity) {
	}

    public void swapEntity(Object item, Object parent, ListParams params,
    		int swapWith) {

    	Assert.notNull(positionProperty, "A positionProperty must be specified.");

    	List<?> items = listInternal(parent, params);
    	Object nextItem = items.get(swapWith);

    	Object pos1 = PropertyUtils.getProperty(item, positionProperty);
    	Object pos2 = PropertyUtils.getProperty(nextItem, positionProperty);

    	PropertyUtils.setProperty(item, positionProperty, pos2);
    	PropertyUtils.setProperty(nextItem, positionProperty, pos1);
    }

	/**
	 * @param setPositionOnSave the setPositionOnSave to set
	 */
	public void setSetPositionOnSave(boolean setPositionOnSave) {
		this.setPositionOnSave = setPositionOnSave;
	}
	
	protected boolean isSetPositionOnSave() {
		return setPositionOnSave;
	}

}