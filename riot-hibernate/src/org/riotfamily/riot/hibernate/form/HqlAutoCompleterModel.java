package org.riotfamily.riot.hibernate.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.forms.element.suggest.AutocompleteTextField;
import org.riotfamily.forms.element.suggest.AutocompleterModel;
import org.riotfamily.riot.hibernate.support.HibernateSupport;

public class HqlAutoCompleterModel extends HibernateSupport 
		implements AutocompleterModel {

	private String hql;
	
	public HqlAutoCompleterModel(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public void setHql(String hql) {
		this.hql = hql;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getSuggestions(String search,
			AutocompleteTextField element, HttpServletRequest request) {
		
		Query query = getSession().createQuery(hql);
		query.setParameter("search", "%" + search  + "%");
		if (hql.indexOf(":parent") != -1) {
			query.setParameter("parent", ScreenContext.get(request).getParent());	
		}
		return query.list();
	}

}
