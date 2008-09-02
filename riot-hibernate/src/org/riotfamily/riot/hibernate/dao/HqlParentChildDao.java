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

import org.hibernate.Query;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.hibernate.support.HibernateUtils;



/**
 * ParentChildDao implementation based on Hibernate.
 */
public class HqlParentChildDao extends HqlDao implements ParentChildDao, 
		CutAndPasteEnabledDao {

    private String parentProperty;
    
	public String getParentProperty() {
		return parentProperty;
	}
	
	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	public Object getParent(Object entity) {
		return PropertyUtils.getProperty(entity, parentProperty);
	}
	
	public void save(Object entity, Object parent) {
		PropertyUtils.setProperty(entity, parentProperty, parent);
    	if (positionProperty != null && setPositionOnSave) {
    		PropertyUtils.setProperty(entity, positionProperty,
    						getNextPosition(parent));
    		
    	}
		getSession().save(entity);
	}
	
	public void delete(Object entity, Object parent) {
		PropertyUtils.setProperty(entity, parentProperty, null);
		getSession().delete(entity);
	}
	
	protected void setQueryParameters(Query query, Object parent, 
			ListParams params) {
		
		super.setQueryParameters(query, parent, params);
		 if (parent != null) {
        	query.setParameter("parent", parent);
        }
	}
	
    protected String getWhereClause(Object parent, ListParams params) {
        StringBuffer sb = new StringBuffer();
        if (parentProperty != null) {
        	sb.append("this.");
       		sb.append(parentProperty);
        	if (parent == null) {
	        	sb.append(" is null");
        	}
        	else {
        		sb.append(" = :parent ");
        	}
        }
        HibernateUtils.appendHql(sb, "and", super.getWhereClause(parent, params));
        return sb.toString();
    }
    
    protected Object getNextPosition(Object parent) {
    	StringBuffer hql = new StringBuffer();
    	hql.append("select max(")
    		.append(positionProperty)
    		.append(") + 1 from ")
    		.append(entityClass.getName())
    		.append(" where ");
    	
    	if (parent != null) {
    		hql.append(parentProperty).append(" = :parent");
    	} else {
    		hql.append(parentProperty).append(" is null");
    	}
    	
    	Query query = getSession().createQuery(hql.toString());
    	if (parent != null) {
    		query.setEntity("parent", parent);
    	}
    	
    	Object res = query.uniqueResult();
    	if (res == null) {
    		res = new Integer(0);
    	}
    	return res;
    }

    public void addChild(Object entity, Object parent) {
    	PropertyUtils.setProperty(entity, parentProperty, parent);
    	if (positionProperty != null && setPositionOnSave) {
    		PropertyUtils.setProperty(entity, positionProperty,
    						getNextPosition(parent));
    		
    	}
    	update(entity);
    }
    
    public void removeChild(Object entity, Object parent) {
    	PropertyUtils.setProperty(entity, parentProperty, null);
    	update(entity);
    }

}