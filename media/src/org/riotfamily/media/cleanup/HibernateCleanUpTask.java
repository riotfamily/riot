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
package org.riotfamily.media.cleanup;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.riotfamily.common.scheduling.HibernateTask;
import org.riotfamily.common.util.Generics;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.store.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateCleanUpTask extends HibernateTask {

	private Logger log = LoggerFactory.getLogger(HibernateCleanUpTask.class);
	
	private FileStore fileStore;
	
	private TransactionTemplate transactionTemplate;
	
	private List<String> fileQueries = Generics.newArrayList();

	private boolean deleteOrphanedFiles = true;
	
	private boolean deleteUnmanagedFiles = true;
	
	public HibernateCleanUpTask(SessionFactory sessionFactory, FileStore fileStore, 
			PlatformTransactionManager tx) {
		
		super(sessionFactory);
		this.fileStore = fileStore;
		this.transactionTemplate = new TransactionTemplate(tx);
		init();
	}

	public void setDeleteOrphanedFiles(boolean deleteOrphanedFiles) {
		this.deleteOrphanedFiles = deleteOrphanedFiles;
	}
	
	public void setDeleteUnmanagedFiles(boolean deleteUnmanagedFiles) {
		this.deleteUnmanagedFiles = deleteUnmanagedFiles;
	}
	
	@Override
	protected void doWithoutResult(final Session session) throws Exception {
		log.info("Media clean-up started.");
		
		if (deleteOrphanedFiles) {
			deleteOrphanedFiles(session);
		}
		
		if (deleteUnmanagedFiles) {
			deleteUnmanagedFiles(session);
		}
		
		log.info("Media clean-up finished.");
	}

	@SuppressWarnings("unchecked")
	private void deleteOrphanedFiles(final Session session) {
		log.info("Looking for orphaned files ...");

		List<Long> fileIds = session.createQuery(
				"select id from " + RiotFile.class.getName()).list();
		
		Set<Long> referencedIds = Generics.newHashSet();
		for (String hql : fileQueries) {
			log.info(hql);
			referencedIds.addAll(session.createQuery(hql).list());
		}

		log.info("Deleting [{}] orphaned files ...", fileIds.size() - referencedIds.size());
		
		for (Long id : fileIds) {
			if (!referencedIds.contains(id)) {
				delete(session, id);
			}
		}
	}
	
	private void deleteUnmanagedFiles(final Session session) {
		log.info("Deleting unmanaged files ...");
		Iterator<String> files = fileStore.iterator();
		while (files.hasNext()) {
			String uri = files.next();
			if (!fileExists(session, uri)) {
				log.debug("Deleting unmanaged file: " + uri);
				files.remove();
			}
		}
	}

	private void delete(final Session session, final Long id) {
		try {
			RiotFile file = transactionTemplate.execute(new TransactionCallback<RiotFile>() {
				public RiotFile doInTransaction(TransactionStatus status) {
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
	
	private void init() {
		Collection<ClassMetadata> allMeta = getSessionFactory().getAllClassMetadata().values();
		for (ClassMetadata meta : allMeta) { 
			for (String name : meta.getPropertyNames()) {
				Type type = meta.getPropertyType(name);
				if (RiotFile.class.isAssignableFrom(type.getReturnedClass())) {
					fileQueries.add(String.format(
							"select %1$s.id from %2$s where %1$s is not null",
							name, meta.getEntityName()));
				}
				else if (type instanceof ComponentType) {
					handleComponentType((ComponentType) type, name, meta.getEntityName());
				}
				else if (type instanceof CollectionType) {
					CollectionType collectionType = (CollectionType) type;
					Type elementType = collectionType.getElementType(
							(SessionFactoryImplementor) getSessionFactory());
					
					if (RiotFile.class.isAssignableFrom(elementType.getReturnedClass())) {
						fileQueries.add(String.format("select file.id from %1$s ref " +
								"join ref.%2$s as file where file is not null",
								meta.getEntityName(), name));	
					}
					else if (elementType instanceof ComponentType) {
						handleCollectionComponentType((ComponentType) elementType, 
								null, name, meta.getEntityName());
					}
				}
			}
		}
	}
	
	private void handleComponentType(ComponentType componentType, String property, 
				String entityName) {
		
		for (int i = 0; i < componentType.getSubtypes().length; i++) {
			Type subtype = componentType.getSubtypes()[i];
			String subProperty = property + "." + componentType.getPropertyNames()[i];
			if (subtype instanceof EntityType 
						&& RiotFile.class.isAssignableFrom(subtype.getReturnedClass())) {
				
				fileQueries.add(String.format("select %1$s.id from %2$s where %1$s is not null",
						subProperty, entityName));
			}
			else if (subtype.isComponentType()) {
				handleComponentType((ComponentType) subtype, subProperty, entityName);
			}
		}
	}

	private void handleCollectionComponentType(ComponentType componentType, String property, String collectionProperty, String entityName) {
		for (int i = 0; i < componentType.getSubtypes().length; i++) {
			Type subtype = componentType.getSubtypes()[i];
			
			String subProperty = property != null ? 
					property + "." + componentType.getPropertyNames()[i] : 
					componentType.getPropertyNames()[i];

			if (subtype instanceof EntityType 
					&& RiotFile.class.isAssignableFrom(subtype.getReturnedClass())) {
				
				fileQueries.add(String.format("select file.id from %2$s ref " +
						"join ref.%3$s as col join col.%1$s file where file is not null",
						subProperty, entityName, collectionProperty));
			}
			else if (subtype.isComponentType()) {
				handleCollectionComponentType((ComponentType) subtype, 
						subProperty, collectionProperty, entityName);
			}
		}
	}
	
}
