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
import java.util.Collections;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class RiotDaoAdapter implements RiotDao {

	public Class<?> getEntityClass() {
		return null;
	}

	/**
	 * Always returns <code>-1</code>.
	 */
	public int getListSize(Object parent, ListParams params) 
			throws DataAccessException {
		
		return -1;
	}

	/**
	 * Always returns <code>null</code>.
	 */
	public String getObjectId(Object entity) {
		return null;
	}

	/**
	 * Always returns <code>Collections.EMPTY_LIST</code>.
	 */
	public Collection<?> list(Object parent, ListParams params) 
			throws DataAccessException {
		
		return Collections.EMPTY_LIST;
	}

	/**
	 * Always throws an InvalidDataAccessApiUsageException.
	 */
	public Object load(String id) throws DataAccessException {
		throw new InvalidDataAccessApiUsageException(
				"Load operations are not supported by this DAO.");
	}

	/**
	 * Always throws an InvalidDataAccessApiUsageException.
	 */
	public void save(Object entity, Object parent) throws DataAccessException {
		throw new InvalidDataAccessApiUsageException(
			"Save operations are not supported by this DAO.");
	}

	/**
	 * Returns the given entity.
	 */
	public Object merge(Object entity) throws DataAccessException {
		return entity;
	}
	
	/**
	 * Returns the entity as-is.
	 */
	public Object update(Object entity) throws DataAccessException {
		return entity;
	}
	
	/**
	 * Always throws an InvalidDataAccessApiUsageException.
	 */
	public void delete(Object entity, Object parent) 
			throws DataAccessException {
		
		throw new InvalidDataAccessApiUsageException(
				"Delete operations are not supported by this DAO.");
	}
	
}
