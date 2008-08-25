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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.SwappableItemDao;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.riotfamily.riot.list.support.EmptyListParams;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.springframework.util.Assert;

/**
 * RiotDao implementation based on Hibernate.
 */
public class HqlDao extends AbstractHqlDao implements SwappableItemDao {

	private Log log = LogFactory.getLog(HqlDao.class);

	private Class<?> entityClass;
	
    private boolean polymorph = true;

    private String select = "this";
    
    private String where;

    private String positionProperty;
    
    private boolean setPositionOnSave;

    
    public HqlDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	@Override
	protected boolean isPolymorph() {
        return polymorph;
    }

    public void setPolymorph(boolean polymorph) {
        this.polymorph = polymorph;
    }

    protected String getSelect() {
    	return select;
    }
    
    public void setSelect(String select) {
		this.select = select;
	}
    
    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

	public void setPositionProperty(String positionProperty) {
		this.positionProperty = positionProperty;
	}

	public void setSetPositionOnSave(boolean setPositionOnSave) {
		this.setPositionOnSave = setPositionOnSave;
	}
	
	protected boolean isSetPositionOnSave() {
		return setPositionOnSave;
	}

    /**
     * Builds a HQL query string to retrieve the maximal position property value
     */
    protected String buildMaxPositionHql(Object parent) {
    	StringBuffer hql = new StringBuffer();
    	hql.append("select max(").append(positionProperty).append(") from ");
    	hql.append(getEntityClass().getName());
    	hql.append(" as this");
    	HibernateUtils.appendHql(
    		hql, "where", getWhereClause(parent, new EmptyListParams()));
    	log.debug(hql);
        return hql.toString();
    }

	protected void setPositionIfNeeded(Object entity, Object parent) {
    	if (setPositionOnSave) {
    		Query query = getSession().createQuery(buildMaxPositionHql(parent));
    		setQueryParameters(query, parent, new EmptyListParams());
    		Number maxPosition = (Number) query.uniqueResult();
    		
    		PropertyUtils.setProperty(entity, positionProperty,
    			new Integer(maxPosition != null? maxPosition.intValue() + 1: 0)); 
    	}
    }

    @Override
    public void save(Object entity, Object parent) {
    	setPositionIfNeeded(entity, parent);
    	super.save(entity, parent);
    }

    public void swapEntity(Object item, Object parent, ListParams params,
    		int swapWith) {

    	Assert.notNull(positionProperty, "A positionProperty must be specified.");

    	List<?> items = listInternal(parent, new ListParamsImpl(params));
    	Object nextItem = items.get(swapWith);

    	Object pos1 = PropertyUtils.getProperty(item, positionProperty);
    	Object pos2 = PropertyUtils.getProperty(nextItem, positionProperty);

    	PropertyUtils.setProperty(item, positionProperty, pos2);
    	PropertyUtils.setProperty(nextItem, positionProperty, pos1);
    }

}