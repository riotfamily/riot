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
package org.riotfamily.media.cleanup;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.riotfamily.common.log.RiotLog;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.riotfamily.common.util.Generics;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.store.FileStore;
import org.riotfamily.riot.hibernate.support.LongConversationTask;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateCleanUpTask extends LongConversationTask {

	private RiotLog log = RiotLog.get(HibernateCleanUpTask.class);
	
	private FileStore fileStore;
	
	private TransactionTemplate transactionTemplate;
	
	private List<String> fileQueries = Generics.newArrayList();

	
	public HibernateCleanUpTask(SessionFactory sessionFactory, FileStore fileStore, 
			PlatformTransactionManager tx) {
		
		super(sessionFactory);
		this.fileStore = fileStore;
		this.transactionTemplate = new TransactionTemplate(tx);
		init();
	}

	@SuppressWarnings("unchecked")
	public void doInHibernate(final Session session) throws Exception {
		
		log.info("Looking for orphaned files ...");

		List<Long> fileIds = session.createQuery(
				"select id from " + RiotFile.class.getName()).list();
		
		Set<Long> referencedIds = Generics.newHashSet();
		for (String hql : fileQueries) {
			log.info(hql);
			referencedIds.addAll(session.createQuery(hql).list());
		}

		log.info("Deleting orphaned files ...");
		
		for (Long id : fileIds) {
			if (!referencedIds.contains(id)) {
				delete(session, id);
			}
		}
		
		log.info("Deleting unmanaged files ...");
		Iterator<String> files = fileStore.iterator();
		while (files.hasNext()) {
			String uri = files.next();
			if (!fileExists(session, uri)) {
				log.debug("Deleting unmanaged file: " + uri);
				files.remove();
			}
		}
		
		log.info("Media clean-up finished.");
	}
	
	private void delete(final Session session, final Long id) {
		try {
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					log.debug("Deleting orphaned file: " + id);
					RiotFile file = (RiotFile) session.load(RiotFile.class, id);
					session.delete(file);
					return null;
				}
			});
		}
		catch (HibernateException e) {
			log.error("Failed to delete RiotFile " + id, e);
		}
	}
	
	public boolean fileExists(Session session, String uri) {
		return session.createQuery("select id from " 
				+ RiotFile.class.getName() 
				+ " where uri = :uri")
				.setParameter("uri", uri)
				.uniqueResult() != null;
	}
	
	@SuppressWarnings("unchecked")
	private void init() {
		Collection<ClassMetadata> allMeta = getSessionFactory().getAllClassMetadata().values();
		for (ClassMetadata meta : allMeta) { 
			for (String name : meta.getPropertyNames()) {
				if (RiotFile.class.isAssignableFrom(meta.getPropertyType(name).getReturnedClass())) {
					fileQueries.add(String.format(
							"select %1$s.id from %2$s where %1$s is not null",
							name, meta.getEntityName()));
				}
			}
		}
		
		Collection<CollectionMetadata> allCollMeta = getSessionFactory().getAllCollectionMetadata().values();
		for (CollectionMetadata meta : allCollMeta) {
			if (RiotFile.class.isAssignableFrom(meta.getElementType().getReturnedClass())) {
				String role = meta.getRole();
				int i = role.lastIndexOf('.');
				String entityName = role.substring(0, i);
				String property = role.substring(i + 1);
				fileQueries.add(String.format("select file.id from %1$s ref " +
						"join ref.%2$s as file where file is not null",
						entityName, property));	
			}
		}
	}
	
}
