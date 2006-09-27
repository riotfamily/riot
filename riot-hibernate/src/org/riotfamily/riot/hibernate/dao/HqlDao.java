package org.riotfamily.riot.hibernate.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.dao.SwappableItemDao;
import org.riotfamily.riot.hibernate.support.HibernateSupport;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.springframework.util.Assert;

/**
 * RiotDao implementation based on Hibernate.
 */
public class HqlDao extends HibernateSupport implements RiotDao, 
		SwappableItemDao {	
	
	public static final String DEFAULT_ID_PROPERTY = "id";
	
    private Class entityClass;

    private boolean polymorph = true;

    private String where;
    
    private String idProperty = DEFAULT_ID_PROPERTY;

    private String positionProperty;
    
    boolean hasWhere;

    private String countHql = "select count(this) from $class as this $where";

    private String hql = "select this from $class as this $where";

    private String preparedCountHql;

    private String preparedHql;
    
    /**
     * @return Returns the hql.
     */
    public final String getHql() {
        return hql;
    }

    /**
     * @param hql The hql to set.
     */
    public final void setHql(String hql) {
        this.hql = hql;
    }

	public String getCountHql() {
		return countHql;
	}

	public void setCountHql(String countHql) {
		this.countHql = countHql;
	}
    
	/**
     * @return Returns the itemClass.
     */
    public final Class getEntityClass() {
        return entityClass;
    }

    /**
     * @param itemClass The itemClass to set.
     */
    public final void setEntityClass(Class itemClass) {
        this.entityClass = itemClass;
    }

    /**
     * @return Returns the polymorph.
     */
    public final boolean isPolymorph() {
        return polymorph;
    }

    /**
     * @param polymorph The polymorph to set.
     */
    public final void setPolymorph(boolean polymorph) {
        this.polymorph = polymorph;
    }

    /**
     * @return Returns the where.
     */
    public final String getWhere() {
        return where;
    }

    /**
     * @param where The where to set.
     */
    public final void setWhere(String where) {
        this.where = where;
    }
    
	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public void setPositionProperty(String positionProperty) {
		this.positionProperty = positionProperty;
	}

	public String getObjectId(Object item) {
		return PropertyUtils.getPropertyAsString(item, idProperty);
	}
	
	/**
     * Returns a list of items.
     */
    public final Collection list(Object parent, ListParams params) {
        return listInternal(parent, params);
    }
    
    protected List listInternal(Object parent, ListParams params) {
    	Query query = createQuery(buildHql(params));
        if (params.getPageSize() > 0) {
            query.setFirstResult(params.getOffset());
            query.setMaxResults(params.getPageSize());
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
        Query query = createQuery(buildCountHql());
        if (params.getFilter() != null) {
            query.setProperties(params.getFilter());
        }
        Number size = (Number) query.uniqueResult();
        if (size == null) {
        	return 0;
        }
        return size.intValue();
    }

    /**
     * Builds a HQL query string to retrive the total number of items.
     */
    protected String buildCountHql() {
        if (preparedCountHql == null) {
            preparedCountHql = countHql.replaceAll("\\$class", 
                    entityClass.getName());
            
            preparedCountHql = preparedCountHql.replaceAll("\\$where",
                    getWhereClause());
        }
        return preparedCountHql;
    }

    /**
     * Builds a HQL query string to retrive a list of items.
     */
    protected String buildHql(ListParams params) {
        if (preparedHql == null) {
            preparedHql = hql.replaceAll("\\$class", entityClass.getName());
            preparedHql = preparedHql.replaceAll("\\$where", getWhereClause());
            preparedHql = preparedHql.replaceAll("\\$and", 
                    hasWhere ? " and " : " where ");
        }
        return preparedHql + getOrderBy(params);
    }

    protected String getWhereClause() {
        StringBuffer sb = new StringBuffer();
        hasWhere = where != null;
        if (hasWhere) {
            sb.append(" where ").append(where);
        }
        if (!polymorph) {
            sb.append(hasWhere ? " and " : " where ");
            sb.append(" (this.class = ");
            sb.append(entityClass.getName());
            sb.append(')');
            hasWhere = true;
        }
        return sb.toString();
    }

    protected String getOrderBy(ListParams params) {
        StringBuffer sb = new StringBuffer();
        if (params.hasOrder()) {
        	sb.append(" order by");
        	Iterator it = params.getOrder().iterator();
        	while (it.hasNext()) {
        		Order order = (Order) it.next();
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
    
    public void save(Object entity, Object parent) {
    	getSession().save(entity);
    }
    
    public void delete(Object entity, Object parent) {
    	getSession().delete(entity);
    }
    
    public Object load(String objectId) {
    	Assert.notNull(objectId, "A non-null id must be passed to load()");
		return HibernateUtils.get(getSession(), entityClass, objectId);
    }
    
    public void update(Object entity) {
		getSession().update(entity);
	}
    
    public void swapEntity(Object item, Object parent, ListParams params, 
    		int swapWith) {
    	
    	Assert.notNull(positionProperty, "A positionProperty must be specified.");
    	
    	List items = listInternal(parent, params);
    	Object nextItem = items.get(swapWith);
    	
    	Object pos1 = PropertyUtils.getProperty(item, positionProperty);
    	Object pos2 = PropertyUtils.getProperty(nextItem, positionProperty);
    	
    	PropertyUtils.setProperty(item, positionProperty, pos2);
    	PropertyUtils.setProperty(nextItem, positionProperty, pos1);
    	
    	getSession().update(item);
    	getSession().update(nextItem);
    }

}