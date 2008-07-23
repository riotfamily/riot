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
package org.riotfamily.media.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.riotfamily.common.util.Generics;
import org.riotfamily.media.model.RiotFile;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Transactional
public class HibernateMediaDao implements MediaDao {

	private Log log = LogFactory.getLog(HibernateMediaDao.class);
	
	private SessionFactory sessionFactory;

	private List<String> fileQueries = Generics.newArrayList();
	
	public HibernateMediaDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		init();
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public RiotFile loadFile(Long id) {
		return (RiotFile) getSession().get(RiotFile.class, id);
	}
		
	public RiotFile findDataByUri(String uri) {
		return (RiotFile) getSession().createQuery("from " 
				+ RiotFile.class.getName() 
				+ " data where data.uri = :uri")
				.setParameter("uri", uri)
				.uniqueResult();
	}
	
	public RiotFile findDataByMd5(String md5) {
		return (RiotFile) getSession().createQuery("from " 
				+ RiotFile.class.getName() 
				+ " data where data.md5 = :md5")
				.setParameter("md5", md5)
				.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public void deleteOrphanedFiles() {
		Set<Long> ids = Generics.newHashSet();
		for (String hql : fileQueries) {
			ids.addAll(getSession().createQuery(hql).list());
		}
		
		Iterator<Long> it = getSession().createQuery(
				"select id from " + RiotFile.class.getName()).iterate();
		
		while (it.hasNext()) {
			Long id = it.next();
			if (!ids.contains(id)) {
				log.info("Deleting orphaned file: " + id);
				deleteFile(id);
			}
		}
	}
	
	public void saveFile(RiotFile file) {
		getSession().save(file);
	}
	
	public void deleteFile(RiotFile file) {
		getSession().delete(file);
	}
	
	public void deleteFile(Long id) {
		getSession().createQuery("delete from " + RiotFile.class.getName() 
				+ " where id = :id").setParameter("id", id).executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	private void init() {
		Collection<ClassMetadata> allMeta = sessionFactory.getAllClassMetadata().values();
		for (ClassMetadata meta : allMeta) { 
			for (String name : meta.getPropertyNames()) {
				if (RiotFile.class.isAssignableFrom(meta.getPropertyType(name).getReturnedClass())) {
					fileQueries.add(String.format(
							"select %1$s.id from %2$s where %1$s is not null",
							name, meta.getEntityName()));
				}
			}
		}
		
		Collection<CollectionMetadata> allCollMeta = sessionFactory.getAllCollectionMetadata().values();
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
