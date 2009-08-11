package org.riotfamily.common.hibernate;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * Convenient base class for callbacks that don't return a result. It also
 * allows the callback code to throw checked exceptions which are automatically
 * wrapped into a {@link RuntimeException}.
 * 
 * @since 9.0
 */
public abstract class HibernateCallbackWithoutResult implements HibernateCallback {

	public final Object doInHibernate(Session session) 
			throws HibernateException, SQLException {
		
		try {
			doWithoutResult(session);
		}
		catch (HibernateException e) {
			throw e;
		}
		catch (SQLException e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	protected abstract void doWithoutResult(Session session) throws Exception;

}
