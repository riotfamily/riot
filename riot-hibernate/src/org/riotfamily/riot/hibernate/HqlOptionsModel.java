package org.riotfamily.riot.hibernate;

import java.util.Collection;

import org.riotfamily.forms.element.support.select.OptionsModel;
import org.riotfamily.riot.hibernate.support.HibernateSupport;

public class HqlOptionsModel extends HibernateSupport implements OptionsModel {

	private String hql;
	
	public void setHql(String hql) {
		this.hql = hql;
	}

	public Collection getOptionValues() {
		return getSession().createQuery(hql).list();
	}

}
