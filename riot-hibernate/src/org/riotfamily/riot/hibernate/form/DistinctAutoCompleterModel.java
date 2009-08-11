package org.riotfamily.riot.hibernate.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.forms.element.suggest.AutocompleteTextField;
import org.riotfamily.forms.element.suggest.AutocompleterModel;
import org.riotfamily.riot.hibernate.support.HibernateSupport;

public class DistinctAutoCompleterModel extends HibernateSupport 
		implements AutocompleterModel {

	private boolean caseSensitive = false;
	
	private String parentProperty;
	
	public DistinctAutoCompleterModel(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getSuggestions(String search,
			AutocompleteTextField element, HttpServletRequest request) {
		
		String property = element.getEditorBinding().getProperty();
		Class<?> clazz = element.getEditorBinding().getBeanClass();
		
		StringBuilder hql = new StringBuilder("select distinct ")
				.append(property).append(" from ").append(clazz.getName())
				.append(" where ");
		
		if (caseSensitive) {
			hql.append(property);
		}
		else {
			hql.append("lower(").append(property).append(')');
			search = search.toLowerCase();
		}
		
		if (parentProperty != null) {
			hql.append(" and ").append(parentProperty).append(".id = :parentId");
		}
		
		hql.append(" like :search order by ").append(property);
		Query query = getSession().createQuery(hql.toString())
				.setParameter("search", "%" + search  + "%");
		
		if (parentProperty != null) {
			query.setParameter("parentId", ScreenContext.get(request).getParentId());	
		}
		return query.list();
	}

}
