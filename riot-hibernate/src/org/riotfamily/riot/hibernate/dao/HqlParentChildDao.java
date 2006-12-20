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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.hibernate.support.HibernateUtils;



/**
 * ParentChildDao implementation based on Hibernate.
 */
public class HqlParentChildDao extends HqlDao implements ParentChildDao, 
		CutAndPasteEnabledDao {

    private Log log = LogFactory.getLog(HqlParentChildDao.class);

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
		getSession().save(entity);
	}
	
	public void delete(Object entity, Object parent) {
		PropertyUtils.setProperty(entity, parentProperty, null);
		getSession().delete(entity);
	}
		
    /**
     * Returns a list of items.
     */
    protected List listInternal(Object parent, ListParams params) {
        Query query = createQuery(buildHql(params));
        if (params.getPageSize() > 0) {
            query.setFirstResult(params.getOffset());
            query.setMaxResults(params.getPageSize());
        }
        if (parent != null) {
        	query.setParameter("parent", parent);
        }
        if (params.getFilter() != null) {
            query.setProperties(params.getFilter());
        }
        return query.list();
    }

    /**
     * Returns the total number of items.
     */
    public int getListSize(Object parent, ListParams params) {
        Query query = createQuery(buildCountHql(params));
        if (parent != null) {
        	query.setParameter("parent", parent);
        }
        log.debug(query.getQueryString());
        Number size = (Number) query.uniqueResult();
        return size != null ? size.intValue() : 0;
    }


    protected String getWhereClause(ListParams params) {
        StringBuffer sb = new StringBuffer();
        boolean hasWhere = false;
        if (parentProperty != null) {
        	sb.append(" where this.");
       		sb.append(parentProperty);
        	if (params.getParentId() == null) {
	        	sb.append(" is null");
        	}
        	else {
        		sb.append(" = :parent ");
        	}
        	hasWhere = true;
        }
        
        String where = getWhere();
        if (where == null && params.getFilter() != null) {
        	where = HibernateUtils.getExampleWhereClause(params.getFilter(), 
        			"this", params.getFilteredProperties());
        }
        if (where != null) {
        	sb.append(hasWhere ? " and " : " where ");
            sb.append(where);
            hasWhere = true;
        }
        
        if (!isPolymorph()) {
        	sb.append(hasWhere ? " and " : " where ");
            sb.append("this.class = ");
            sb.append(getEntityClass().getName());
        }
        return sb.toString();
    }
    
    public void addChild(Object entity, Object parent) {
    	PropertyUtils.setProperty(entity, parentProperty, parent);
    	update(entity);
    }
    
    public void removeChild(Object entity, Object parent) {
    	PropertyUtils.setProperty(entity, parentProperty, null);
    	update(entity);
    }

}