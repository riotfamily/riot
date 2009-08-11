package org.riotfamily.riot.hibernate.dao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.core.dao.CutAndPaste;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Hierarchy;
import org.riotfamily.riot.hibernate.support.HibernateUtils;



/**
 * ParentChildDao implementation based on Hibernate.
 */
public class HqlParentChildDao extends HqlDao implements Hierarchy, 
		CutAndPaste {

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