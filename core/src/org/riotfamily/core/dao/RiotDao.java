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
package org.riotfamily.core.dao;

import java.util.Collection;

import org.springframework.dao.DataAccessException;

/**
 * Interface that provides access to an underlying data store.
 */
public interface RiotDao {
	
	/**
	 * Returns the class that is accessed by the DAO.
	 */
	public Class<?> getEntityClass();

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
	 * Updates the given entity.
	 */
	public Object update(Object entity) throws DataAccessException;

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
	public Collection<?> list(Object parent, ListParams params) throws DataAccessException;
	
	/**
	 * Returns the total number of entities.
	 */
	public int getListSize(Object parent, ListParams params) throws DataAccessException;

}
