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

public class TreeDao implements RiotDao, Tree {

	private RiotDao rootDao;
	
	private Hierarchy nodeDao;
	
	public void setRootDao(RiotDao rootDao) {
		this.rootDao = rootDao;
	}

	public void setNodeDao(Hierarchy nodeDao) {
		this.nodeDao = nodeDao;
	}

	public Object getParentNode(Object node) {
		return nodeDao.getParent(node);
	}

	public boolean hasChildren(Object node, Object parent, ListParams params) {
		return !nodeDao.list(node != null ? node : parent, params).isEmpty();
	}

	public Class<?> getEntityClass() {
		return rootDao.getEntityClass();
	}

	public String getObjectId(Object entity) {
		if (nodeDao.getEntityClass().isInstance(entity)) {
			return "$" + nodeDao.getObjectId(entity);
		}
		return rootDao.getObjectId(entity);
	}

	public Object load(String id) throws DataAccessException {
		if (id.startsWith("$")) {
			return nodeDao.load(id.substring(1));
		}
		return rootDao.load(id);
	}

	public Object update(Object entity) throws DataAccessException {
		return getDao(entity, null).update(entity);
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		getDao(entity, parent).save(entity, parent);
	}

	public void delete(Object entity, Object parent) throws DataAccessException {
		getDao(entity, parent).delete(entity, parent);
		
	}

	public Collection<?> list(Object parent, ListParams params)
			throws DataAccessException {
		
		return getDao(null, parent).list(parent, params);
	}

	public int getListSize(Object parent, ListParams params)
			throws DataAccessException {
		
		return getDao(null, parent).getListSize(parent, params);
	}

	private RiotDao getDao(Object entity, Object parent) {
		if (nodeDao.getEntityClass().isInstance(parent)
				|| nodeDao.getEntityClass().isInstance(entity)) {
			
			return nodeDao;
		}
		return rootDao;
	}
}
