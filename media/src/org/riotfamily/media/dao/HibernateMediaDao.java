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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.media.model.RiotFile;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
@Transactional
public class HibernateMediaDao implements MediaDao {

	private SessionFactory sessionFactory;

	public HibernateMediaDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public RiotFile loadFile(Long id) {
		return (RiotFile) getSession().get(RiotFile.class, id);
	}
		
	public RiotFile findFileByUri(String uri) {
		return (RiotFile) getSession().createQuery("from " 
				+ RiotFile.class.getName() 
				+ " where uri = :uri")
				.setParameter("uri", uri)
				.uniqueResult();
	}
	
	public RiotFile findFileByMd5(String md5) {
		return (RiotFile) getSession().createQuery("from " 
				+ RiotFile.class.getName() 
				+ " where md5 = :md5")
				.setParameter("md5", md5)
				.uniqueResult();
	}
	
	public void saveFile(RiotFile file) {
		getSession().save(file);
	}
	
	public void deleteFile(RiotFile file) {
		getSession().delete(file);
	}
	
}
