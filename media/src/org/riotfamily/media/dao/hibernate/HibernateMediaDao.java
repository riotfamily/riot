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
package org.riotfamily.media.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.media.dao.MediaDao;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.data.FileData;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class HibernateMediaDao implements MediaDao {

	private HibernateHelper hibernate;

	public HibernateMediaDao(SessionFactory sessionFactory) {
		this.hibernate = new HibernateHelper(sessionFactory);
	}
	
	public RiotFile loadFile(Long id) {
		return (RiotFile) hibernate.get(RiotFile.class, id);
	}
		
	public FileData findDataByUri(String uri) {
		Query query = hibernate.createQuery("from " + FileData.class.getName() 
				+ " data where data.uri = :uri");
		
		hibernate.setParameter(query, "uri", uri);
		return (FileData) hibernate.uniqueResult(query);
	}
	
	public FileData findDataByMd5(String md5) {
		Query query = hibernate.createQuery("from " + FileData.class.getName() 
				+ " data where data.md5 = :md5");
		
		hibernate.setParameter(query, "md5", md5);
		return (FileData) hibernate.uniqueResult(query);
	}
	
	public List<FileData> findStaleData() {
		Query query = hibernate.createQuery("from " + FileData.class.getName() 
				+ " data where data.files is empty");
		
		return hibernate.list(query);
	}
	
	public void saveFile(RiotFile file) {
		hibernate.save(file);
	}
	
	public void deleteFile(RiotFile file) {
		hibernate.delete(file);
	}
	
	public void deleteData(FileData data) {
		Assert.isTrue(data.getFiles().isEmpty(), "Can't delete FileData " +
				"because it is still in use.");
		
		hibernate.delete(data);
	}
}
