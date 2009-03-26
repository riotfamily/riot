/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.util.RiotLog;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Class similar to Spring's HibernateTemplate that simplifies working with
 * long conversations. This class can be considered as alternative to using
 * an AOP HibernateInterceptor.
 * 
 * @see <a href="http://www.hibernate.org/hib_docs/v3/reference/en/html/transactions.html#transactions-basics-apptx">Hibernate Reference - Long conversations</a>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class LongConversationTemplate extends HibernateAccessor {

	private RiotLog log = RiotLog.get(LongConversationTemplate.class);
	
	public LongConversationTemplate(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public final void execute(LongConversationCallback callback) throws Exception {
		Session session = SessionFactoryUtils.getSession(getSessionFactory(), 
				getEntityInterceptor(), getJdbcExceptionTranslator());
		
		SessionHolder sessionHolder = (SessionHolder) 
				TransactionSynchronizationManager.getResource(getSessionFactory());

		boolean existingTransaction = (sessionHolder != null && sessionHolder.containsSession(session));
		if (existingTransaction) {
			log.debug("Found thread-bound Session for HibernateInterceptor");
		}
		else {
			if (sessionHolder != null) {
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
			callback.doInHibernate(session);
			flushIfNecessary(session, existingTransaction);
		}
		catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		finally {
			if (existingTransaction) {
				log.debug("Not closing pre-bound Hibernate Session after HibernateInterceptor");
				disableFilters(session);
				if (previousFlushMode != null) {
					session.setFlushMode(previousFlushMode);
				}
			}
			else {
				SessionFactoryUtils.closeSession(session);
				if (sessionHolder == null || sessionHolder.doesNotHoldNonDefaultSession()) {
					TransactionSynchronizationManager.unbindResource(getSessionFactory());
				}
			}
		}
	}
}
