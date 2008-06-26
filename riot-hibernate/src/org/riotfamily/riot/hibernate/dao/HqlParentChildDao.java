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
import org.riotfamily.common.beans.PropertyUtils;
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
		super.save(entity, parent);
	}
	
	public void delete(Object entity, Object parent) {
		PropertyUtils.setProperty(entity, parentProperty, null);
		super.delete(entity, parent);
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
    
    public void addChild(Object entity, Object parent) {
    	PropertyUtils.setProperty(entity, parentProperty, parent);
    	setPositionIfNeeded(entity, parent);
    }
    
    /**
     * This method does nothing. Before the parentProperty was set to null here.
     * But since the only call is from the Clipboard's pasteCut method, which
     * calls this after the addChild is called, setting the parentProperty to
     * null won't have the desired effect.
     */
    public void removeChild(Object entity, Object parent) {
    }

}