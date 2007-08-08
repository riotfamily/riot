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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.hibernate.security;

import java.util.Collection;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.security.LoginManager;
import org.springframework.dao.DataAccessException;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class RiotUserDao implements RiotDao {
	
	private RiotDao dao;
	

	public void setDao(RiotDao dao) {
		this.dao = dao;
	}

	public void delete(Object entity, Object parent) {
		dao.delete(entity, parent);
		User user = (User) entity;
		LoginManager.removeUser(user.getId());
	}
	
	public void update(Object entity) {		
		dao.update(entity);
		User user = (User) entity;
		LoginManager.updateUser(user);
	}

	public Class getEntityClass() {
		return dao.getEntityClass();
	}

	public int getListSize(Object parent, ListParams params)
			throws DataAccessException {
		
		return dao.getListSize(parent, params);
	}

	public String getObjectId(Object entity) {		
		return dao.getObjectId(entity);
	}

	public Collection list(Object parent, ListParams params) 
			throws DataAccessException {
		
		return dao.list(parent, params);
	}

	public Object load(String id) throws DataAccessException {		
		return dao.load(id);
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		dao.save(entity, parent);		
	}
	
	

}
