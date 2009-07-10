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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.core.dao.CutAndPasteEnabledDao;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.ParentChildDao;
import org.riotfamily.riot.hibernate.support.HibernateUtils;



/**
 * ParentChildDao implementation based on Hibernate.
 */
public class HqlParentChildDao extends HqlDao implements ParentChildDao, 
		CutAndPasteEnabledDao {

    private String parentProperty;
    
    private boolean parentPropertyMapped;
    
	public HqlParentChildDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public String getParentProperty() {
		return parentProperty;
	}
	
	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	@Override
	protected final void initDao() throws Exception {
		if (parentProperty != null) {
			parentPropertyMapped = HibernateUtils.isPersistentProperty(
					getSessionFactory(), getEntityClass(), parentProperty);
		}
		initParentChildDao();
	}
	
	protected void initParentChildDao() throws Exception {
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
	
    protected String getWhereClause(Object parent, ListParams params) {
        StringBuffer sb = new StringBuffer();
        if (parentPropertyMapped) {
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
        
        return sb.toString().replaceAll(":parent\\.(\\w+)", ":parent_$1");
    }
    
    protected void setQueryParameters(Query query, Object parent, 
			ListParams params) {
		
    	super.setQueryParameters(query, parent, params);
    	if (parent != null) {
			for (String param : query.getNamedParameters()) {
				Matcher m = Pattern.compile("parent(?:_(\\w+))?").matcher(param);
				if (m.matches()) {
					Object value = parent;
					String nested = m.group(1);
					if (nested != null) {
						value = PropertyUtils.getProperty(parent, nested);
					}
					query.setParameter(param, value);
				}
			}
    	}
	}

    public boolean canCut(Object entity) {
    	return true;
    }
    
	public void cut(Object entity, Object parent) {
	}

	public boolean canPasteCut(Object entity, Object target) {
		return true;
	}
	
	public void pasteCut(Object entity, Object parent) {
		PropertyUtils.setProperty(entity, parentProperty, parent);
		setPositionIfNeeded(entity, parent);
	}

}