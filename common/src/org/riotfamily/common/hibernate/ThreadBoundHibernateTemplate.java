/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.hibernate;

import java.sql.SQLException;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger log = LoggerFactory.getLogger(ThreadBoundHibernateTemplate.class);
	
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
