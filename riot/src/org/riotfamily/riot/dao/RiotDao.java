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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.dao;

import java.util.Collection;

import org.springframework.dao.DataAccessException;

/**
 * Interface that provides access to an underlying data store.
 */
public interface RiotDao {
	
	/**
	 * Returns the class that is accessed by the dao.
	 */
	public Class getEntityClass();

	/**
	 * Returns the id of the given entity. Implementors will most likely need
	 * to perform a type conversion in order to return a String representation.
	 * The returned String must be parseable by the {@link #load(String) load()}
	 * method.
	 */
	public String getObjectId(Object entity);
	
	/**
	 * Returns the entity with the given id.
	 */
	public Object load(String id) throws DataAccessException;
	
	/**
	 * Reattaches the given entity. This method is invoked before an object
	 * that has been loaded in a previous request is modified. Implementors
	 * can use this method to perform modification checks or to reattach the
	 * object to a persistence session.  
	 */
	public void reattach(Object entity) throws DataAccessException;
	
	/**
	 * Updates the given entity. This method is invoked after an object has
	 * been modified. Session-based implementations (like Hibernate or JPA)
	 * will usually do nothing in this method, as changes are automatically
	 * written to the database when an object has been modified within a 
	 * transaction.
	 */
	public void update(Object entity) throws DataAccessException;

	/**
	 * Saves the given entity.
	 */
	public void save(Object entity, Object parent) throws DataAccessException;
	
	/**
	 * Deletes the given entity.
	 */
	public void delete(Object entity, Object parent) throws DataAccessException;
	
	/**
	 * Returns a list of entities.
	 */
	public Collection list(Object parent, ListParams params) throws DataAccessException;
	
	/**
	 * Returns the total number of entities.
	 */
	public int getListSize(Object parent, ListParams params) throws DataAccessException;

}
