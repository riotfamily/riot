package org.riotfamily.common.hibernate;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;

public interface SessionFactoryAwareInterceptor extends Interceptor {

	public void setSessionFactory(SessionFactory sessionFactory);

}
