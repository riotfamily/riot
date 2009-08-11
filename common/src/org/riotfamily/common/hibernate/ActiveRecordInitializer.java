package org.riotfamily.common.hibernate;

import org.hibernate.SessionFactory;

/**
 * Class to set the static SessionFactory reference for {@link ActiveRecord}s.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class ActiveRecordInitializer {

	public ActiveRecordInitializer(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public static void setSessionFactory(SessionFactory sessionFactory) {
		ActiveRecord.setSessionFactory(sessionFactory);
	}
	
}
