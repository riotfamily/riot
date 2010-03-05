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

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Swapping;
import org.riotfamily.core.screen.list.ListParamsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * RiotDao implementation based on Hibernate.
 */
public class HqlDao extends AbstractHqlDao implements Swapping {

	private Logger log = LoggerFactory.getLogger(HqlDao.class);

	private Class<?> entityClass;
	
    private boolean polymorph = true;

    private String select = "this";
    
    private String where;

    private String positionProperty;
    
    private boolean setPositionOnSave;

    private boolean invertSorting;
    
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

    @Override
	protected String getSelect() {
    	return select;
    }
    
    public void setSelect(String select) {
		this.select = select;
	}
    
    @Override
	public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where != null ? "(" + where + ")" : null;
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
	
	public void setInvertSorting(boolean invertSorting) {
		this.invertSorting = invertSorting;
	}

    /**
     * Builds a HQL query string to retrieve the maximal position property value
     */
    protected String buildMaxPositionHql(Object parent) {
    	StringBuilder hql = new StringBuilder();
    	hql.append("select max(").append(positionProperty).append(") from ");
    	hql.append(getEntityClass().getName());
    	hql.append(" as this");
    	HqlUtils.appendHql(
    		hql, "where", getWhereClause(parent, new ListParamsImpl()));
    	log.debug(hql.toString());
        return hql.toString();
    }

	protected void setPositionIfNeeded(Object entity, Object parent) {
    	if (setPositionOnSave) {
    		Query query = getSession().createQuery(buildMaxPositionHql(parent));
    		setQueryParameters(query, parent, new ListParamsImpl());
    		Number maxPosition = (Number) query.uniqueResult();
    		
    		PropertyUtils.setProperty(entity, positionProperty,
    				Integer.valueOf(maxPosition != null ? maxPosition.intValue() + 1 : 0)); 
    	}
    }

	@Override
	protected String getOrderBy(ListParams params) {
        if (positionProperty != null) {
        	return invertSorting ? positionProperty + " desc" : positionProperty;	
        }
        return super.getOrderBy(params);
    }
	
    @Override
    public void save(Object entity, Object parent) {
    	setPositionIfNeeded(entity, parent);
    	super.save(entity, parent);
    }

    
    public boolean canSwap(Object entity, Object parent,
    		ListParams params, int swapWith) {
    	
    	List<?> items = listInternal(parent, new ListParamsImpl(params));
    	int i = items.indexOf(entity) + swapWith;
    	return i >= 0 && i < items.size();
    }
    
    public void swapEntity(Object entity, Object parent, 
    		ListParams params, int swapWith) {

    	Assert.notNull(positionProperty, "A positionProperty must be specified.");

    	List<?> items = listInternal(parent, new ListParamsImpl(params));
    	int i = items.indexOf(entity);
    	Object nextItem = items.get(i + swapWith);

    	Object pos1 = PropertyUtils.getProperty(entity, positionProperty);
    	Object pos2 = PropertyUtils.getProperty(nextItem, positionProperty);

    	PropertyUtils.setProperty(entity, positionProperty, pos2);
    	PropertyUtils.setProperty(nextItem, positionProperty, pos1);
    }

}