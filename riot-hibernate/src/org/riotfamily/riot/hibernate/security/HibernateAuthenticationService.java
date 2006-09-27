package org.riotfamily.riot.hibernate.security;

import org.riotfamily.riot.security.AuthenticationService;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateAuthenticationService extends HibernateDaoSupport 
		implements AuthenticationService {

	public String authenticate(String username, String password) {
		User user = (User) getHibernateTemplate().get(User.class, username);
		if (user != null && user.isvalidPassword(password)) {
			return user.getId();
		}
		return null;
	}

}
