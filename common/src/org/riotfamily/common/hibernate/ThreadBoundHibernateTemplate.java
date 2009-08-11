package org.riotfamily.common.hibernate;

import java.sql.SQLException;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.util.RiotLog;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * Subclass of {@link HibernateTemplate} that binds the Hibernate Session to
 * the current thread.
 */
public class ThreadBoundHibernateTemplate extends HibernateTemplate {

	private RiotLog log = RiotLog.get(ThreadBoundHibernateTemplate.class);
	
	public ThreadBoundHibernateTemplate() {
		super();
	}

	public ThreadBoundHibernateTemplate(SessionFactory sessionFactory, boolean allowCreate) {
		super(sessionFactory, allowCreate);
	}

	public ThreadBoundHibernateTemplate(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected Object doExecute(HibernateCallback action, boolean enforceNewSession, boolean enforceNativeSession)
			throws DataAccessException {

		Assert.notNull(action, "Callback object must not be null");

		Session oldSession = null;
		Session session = (enforceNewSession ?
				SessionFactoryUtils.getNewSession(getSessionFactory(), getEntityInterceptor()) 
				: getSession());
		
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());

		boolean existingTransaction = !enforceNewSession &&
				(!isAllowCreate() || (sessionHolder != null && sessionHolder.containsSession(session)));
		
		if (existingTransaction) {
			log.debug("Found thread-bound Session for LongConversationTemplate");
		}
		else {
			if (sessionHolder != null) {
				oldSession = sessionHolder.getSession();
				sessionHolder.addSession(session);
			}
			else {
				TransactionSynchronizationManager.bindResource(getSessionFactory(), new SessionHolder(session));
			}
		}

		FlushMode previousFlushMode = null;
		try {
			previousFlushMode = applyFlushMode(session, existingTransaction);
			enableFilters(session);
			Session sessionToExpose = (enforceNativeSession || isExposeNativeSession() ? session : createSessionProxy(session));
			Object result = action.doInHibernate(sessionToExpose);
			flushIfNecessary(session, existingTransaction);
			return result;
		}
		catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		catch (SQLException ex) {
			throw convertJdbcAccessException(ex);
		}
		catch (RuntimeException ex) {
			// Callback code threw application exception...
			throw ex;
		}
		finally {
			if (existingTransaction) {
				log.debug("Not closing pre-bound Hibernate Session after HibernateTemplate");
				disableFilters(session);
				if (previousFlushMode != null) {
					session.setFlushMode(previousFlushMode);
				}
			}
			else {
				SessionFactoryUtils.closeSession(session);
				if (oldSession != null) {
					sessionHolder.addSession(oldSession);
				}
				else {
					TransactionSynchronizationManager.unbindResource(getSessionFactory());
				}
			}
		}
	}
	
}
