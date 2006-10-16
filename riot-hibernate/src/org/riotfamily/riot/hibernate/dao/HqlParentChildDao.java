package org.riotfamily.riot.hibernate.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.CutAndPasteEnabledDao;
import org.riotfamily.riot.dao.ListParams;



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
        /*
        if (params.getFilter() != null) {
            query.setProperties(params.getFilter());
        }
        */
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
        hasWhere = false;
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
        if (getWhere() != null) {
        	sb.append(hasWhere ? " and " : " where ");
            sb.append(getWhere());
            hasWhere = true;
        }
        if (!isPolymorph()) {
        	sb.append(hasWhere ? " and " : " where ");
            sb.append("this.class = ");
            sb.append(getEntityClass().getName());
            hasWhere = true;
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