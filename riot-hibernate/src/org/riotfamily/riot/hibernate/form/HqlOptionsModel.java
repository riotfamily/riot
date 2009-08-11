package org.riotfamily.riot.hibernate.form;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.riot.hibernate.support.HibernateSupport;

public class HqlOptionsModel extends HibernateSupport implements OptionsModel {

	private String hql;
	
	public HqlOptionsModel(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public void setHql(String hql) {
		this.hql = hql;
	}

	public Collection<?> getOptionValues(Element element) {
		return getSession().createQuery(hql).list();
	}

}
