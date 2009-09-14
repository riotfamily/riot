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
 *   alf
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.dao.cleanup;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Content;
import org.riotfamily.riot.hibernate.support.LongConversationTask;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateDelayedContentCleanupTask extends LongConversationTask {
	
	private static final RiotLog log =
		RiotLog.get(HibernateDelayedContentCleanupTask.class); 

	private TransactionTemplate transactionTemplate;
	
	private ComponentDao componentDao;
	
	private DelayedContentCleanupQueue queue;
	
	private long delay;
	
	private void deleteContent(final Long contentId, final Session session) {
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				
				try {
					Content liveList = componentDao.loadContent(contentId);
					componentDao.deleteContent(liveList);
					// Flush session to force any exceptions to occur now.
					session.flush();
					log.debug("Content with id %d deleted.", contentId);
				} catch (RuntimeException e) {
					log.warn("Failed to delete content with id %d.", e,
						contentId);
				}
				return null;
			}
		});
	}
	
	public HibernateDelayedContentCleanupTask(SessionFactory sessionFactory, 
		PlatformTransactionManager tx) {
		
		super(sessionFactory);
		this.transactionTemplate = new TransactionTemplate(tx);
	}

	public void doInHibernate(Session session) throws Exception {
		DelayedContentCleanupQueue.Item item;
		
		log.trace("Wakeup!");
		while((item = queue.get(delay)) != null) {
			if (log.isTraceEnabled()) {
				log.trace("About to delete content with id %d.",
					item.getContentId());
			}
			deleteContent(item.getContentId(), session);
		}
	}
	
	public void setQueue(DelayedContentCleanupQueue queue) {
		this.queue = queue;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void setComponentDao(ComponentDao componentDao) {
		this.componentDao = componentDao;
	}
}
