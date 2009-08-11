package org.riotfamily.media.cleanup;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.riotfamily.common.scheduling.HibernateTask;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.store.FileStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateCleanUpTask extends HibernateTask {

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
	protected void doWithoutResult(final Session session) throws Exception {
		
		log.info("Looking for orphaned files ...");

		List<Long> fileIds = session.createQuery(
				"select id from " + RiotFile.class.getName()).list();
		
		Set<Long> referencedIds = Generics.newHashSet();
		for (String hql : fileQueries) {
			log.info(hql);
			referencedIds.addAll(session.createQuery(hql).list());
		}

		log.info("Deleting [%s] orphaned files ...", fileIds.size() - referencedIds.size());
		
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
			RiotFile file = (RiotFile) transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					log.debug("Deleting orphaned file: " + id);
					RiotFile file = (RiotFile) session.load(RiotFile.class, id);
					session.delete(file);
					return file;
				}
			});
			session.evict(file);
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
