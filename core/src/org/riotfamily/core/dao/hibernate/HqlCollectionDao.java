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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.core.dao.CutAndPaste;
import org.riotfamily.core.dao.Hierarchy;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Sortable;
import org.springframework.util.Assert;

/**
 * RiotDao implementation that loads a bean and returns one of the bean's
 * properties as (filtered) collection.
 */
public class HqlCollectionDao extends AbstractHqlDao implements
		Sortable, Hierarchy, CutAndPaste {

	private boolean polymorph = true;

	private String select = "this";
	
	private String where;

	private Class<?> entityClass;

	private Class<?> parentClass;

	private String parentProperty;

	private String collectionProperty;

	public HqlCollectionDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	protected String getSelect() {
    	return select;
    }
    
    public void setSelect(String select) {
		this.select = select;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setPolymorph(boolean polymorph) {
		this.polymorph = polymorph;
	}
	
	@Override
	protected boolean isPolymorph() {
		return polymorph;
	}

	public void setWhere(String string) {
		where = string;
	}
	
	@Override
	protected String getWhere() {
		return where;
	}

	public void setParentClass(Class<?> parentClass) {
		this.parentClass = parentClass;
	}

	public void setCollectionProperty(String property) {
		this.collectionProperty = property;
	}

	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	@Override
	protected void initDao() throws Exception {
		Assert.notNull(collectionProperty, "The collectionProperty must be set");
		if (entityClass == null) {
			Assert.notNull(parentClass, "Eiter entityClass or parentClass must be set");
			entityClass = PropertyUtils.getCollectionPropertyType(parentClass, collectionProperty);
		}
		if (parentClass == null) {
			Assert.notNull(parentProperty, "Eiter paretnClass or parentProperty must be set");
			parentClass = PropertyUtils.getPropertyType(entityClass, parentProperty);
		}
	}

	protected String getRole() {
		return parentClass.getName() + "." + collectionProperty;
	}
	
	public Object getParent(Object entity) {
		if (parentProperty != null) {
			return PropertyUtils.getProperty(entity, parentProperty);
		}
		StringBuilder hql = new StringBuilder();
		hql.append("select parent from ").append(parentClass.getName());
		hql.append(" parent join parent.").append(collectionProperty);
		hql.append(" child where child = :child");

		Query query = getSession().createQuery(hql.toString());
		query.setMaxResults(1);
		query.setParameter("child", entity);
		return query.uniqueResult();
	}

	@Override
	public void save(Object entity, Object parent) {
		if (parentProperty != null) {
			PropertyUtils.setProperty(entity, parentProperty, parent);
		}
		if (parent != null) {
			getCollection(parent).add(entity);
		}
		super.save(entity, parent);
	}

	@Override
	public void delete(Object entity, Object parent) {
		if (parent != null) {
			getCollection(parent).remove(entity);
		}
		super.delete(entity, parent);
	}

	@SuppressWarnings("unchecked")
	protected Collection<Object> getCollection(Object parent) {
		return (Collection<Object>) PropertyUtils.getProperty(parent,
				collectionProperty);
	}

	@Override
	protected void appendFromClause(StringBuilder hql, ListParams params) {
	}
	
	@Override
	protected List<?> listInternal(Object parent, ListParams params) {
		Collection<?> c = getCollection(parent);
		if (c != null) {
			Query query = getSession().createFilter(c, buildHql(parent, params));
	    	setQueryParameters(query, parent, params);
	        if (params.getPageSize() > 0) {
	            query.setFirstResult(params.getOffset());
	            query.setMaxResults(params.getPageSize());
	        }
			return query.list();
		}
		return Collections.emptyList();
	}

	@Override
	public int getListSize(Object parent, ListParams params) {
		Collection<?> c = getCollection(parent);
		if (c != null) {
			Query query = getSession().createFilter(c, buildCountHql(parent, params));
	        setQueryParameters(query, parent, params);
	        Number size = (Number) query.uniqueResult();
	        if (size == null) {
	        	return 0;
	        }
	        return size.intValue();
		}
		return 0;
	}

	public boolean canCut(Object entity) {
		return true;
	}

	public void cut(Object entity, Object parent) {
		getCollection(parent).remove(entity);
		if (parentProperty != null) {
			PropertyUtils.setProperty(entity, parentProperty, null);
		}
	}

	public boolean canPasteCut(Object entity, Object target) {
		return true;
	}

	public void pasteCut(Object entity, Object parent) {
		getCollection(parent).add(entity);
		if (parentProperty != null) {
			PropertyUtils.setProperty(entity, parentProperty, parent);
		}
	}

}
