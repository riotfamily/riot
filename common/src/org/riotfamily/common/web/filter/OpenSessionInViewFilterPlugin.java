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
package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * FilterPlugin that binds a Hibernate Session to the thread for the entire
 * processing of the request just like Spring's OpenSessionInViewFilter.
 * @since 8.0
 */
public class OpenSessionInViewFilterPlugin extends FilterPlugin {

	private Logger log = LoggerFactory.getLogger(OpenSessionInViewFilterPlugin.class);
	
	private SessionFactory sessionFactory;

	private boolean singleSession = true;
	
	private FlushMode flushMode = FlushMode.MANUAL;
	
	
	public OpenSessionInViewFilterPlugin(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	/**
	 * Set whether to use a single session for each request. Default is "true".
	 * <p>If set to "false", each data access operation or transaction will use
	 * its own session (like without Open Session in View). Each of those
	 * sessions will be registered for deferred close, though, actually
	 * processed at request completion.
	 * @see SessionFactoryUtils#initDeferredClose
	 * @see SessionFactoryUtils#processDeferredClose
	 */
	public final void setSingleSession(boolean singleSession) {
		this.singleSession = singleSession;
	}

	/**
	 * Specify the Hibernate FlushMode to apply to this filter's
	 * {@link org.hibernate.Session}. Only applied in single session mode.
	 * <p>Can be populated with the corresponding constant name in XML bean
	 * definitions: e.g. "AUTO".
	 * <p>The default is "MANUAL". Specify "AUTO" if you intend to use
	 * this filter without service layer transactions.
	 * @see org.hibernate.Session#setFlushMode
	 * @see org.hibernate.FlushMode#MANUAL
	 * @see org.hibernate.FlushMode#AUTO
	 */
	public final void setFlushMode(FlushMode flushMode) {
		this.flushMode = flushMode;
	}
	
	@Override
	public void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
		boolean participate = false;

		if (singleSession) {
			// single session mode
			if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
				// Do not modify the Session: just set the participate flag.
				participate = true;
			}
			else {
				log.debug("Opening single Hibernate Session in OpenSessionInViewFilter");
				Session session = getSession(sessionFactory);
				TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			}
		}
		else {
			// deferred close mode
			if (SessionFactoryUtils.isDeferredCloseActive(sessionFactory)) {
				// Do not modify deferred close: just set the participate flag.
				participate = true;
			}
			else {
				SessionFactoryUtils.initDeferredClose(sessionFactory);
			}
		}

		try {
			filterChain.doFilter(request, response);
		}

		finally {
			if (!participate) {
				if (singleSession) {
					// single session mode
					SessionHolder sessionHolder = 
							(SessionHolder) TransactionSynchronizationManager
							.unbindResource(sessionFactory);
					
					log.debug("Closing single Hibernate Session in OpenSessionInViewFilter");
					closeSession(sessionHolder.getSession(), sessionFactory);
				}
				else {
					// deferred close mode
					SessionFactoryUtils.processDeferredClose(sessionFactory);
				}
			}
		}
	}
	
	/**
	 * Get a Session for the SessionFactory that this filter uses.
	 * Note that this just applies in single session mode!
	 * <p>The default implementation delegates to the
	 * <code>SessionFactoryUtils.getSession</code> method and
	 * sets the <code>Session</code>'s flush mode to "NEVER".
	 * <p>Can be overridden in subclasses for creating a Session with a
	 * custom entity interceptor or JDBC exception translator.
	 * @param sessionFactory the SessionFactory that this filter uses
	 * @return the Session to use
	 * @throws DataAccessResourceFailureException if the Session could not be created
	 * @see org.springframework.orm.hibernate3.SessionFactoryUtils#getSession(SessionFactory, boolean)
	 * @see org.hibernate.FlushMode#MANUAL
	 */
	protected Session getSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		if (flushMode != null) {
			session.setFlushMode(flushMode);
		}
		return session;
	}

	/**
	 * Close the given Session.
	 * Note that this just applies in single session mode!
	 * <p>Can be overridden in subclasses, e.g. for flushing the Session before
	 * closing it. See class-level javadoc for a discussion of flush handling.
	 * Note that you should also override getSession accordingly, to set
	 * the flush mode to something else than MANUAL.
	 * @param session the Session used for filtering
	 * @param sessionFactory the SessionFactory that this filter uses
	 */
	protected void closeSession(Session session, SessionFactory sessionFactory) {
		SessionFactoryUtils.closeSession(session);
	}
}
