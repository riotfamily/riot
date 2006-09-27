package org.riotfamily.riot.hibernate.security;

import org.riotfamily.riot.security.session.SessionData;
import org.riotfamily.riot.security.session.SessionDataStore;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateSessionDataStore extends HibernateDaoSupport 
		implements SessionDataStore {

	public SessionData loadSessionData(String principal) {
		return (SessionData) getHibernateTemplate().get(
				SessionData.class, principal);
	}

	public void storeSessionData(SessionData sessionData) {
		getHibernateTemplate().saveOrUpdate(sessionData);
	}

	

}
