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
package org.riotfamily.core.security.session;

import java.util.Collection;

import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.Searchable;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.auth.RiotUserDao;
import org.springframework.dao.DataAccessException;

/**
 * Wrapper that invokes {@link UserHolder#updateUser(String, RiotUser)} whenever
 * a user is updated or deleted.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RiotUserDaoWrapper implements RiotUserDao, Searchable {

	private RiotUserDao wrappedInstance;
	
	public RiotUserDaoWrapper(RiotUserDao userDao) {
		this.wrappedInstance = userDao;
	}
	
	public RiotUser findUserByCredentials(final String username, final String password) {
		return wrappedInstance.findUserByCredentials(username, password);
	}
	
	public RiotUser findUserById(final String userId) {
		return wrappedInstance.findUserById(userId);
	}
	
	public void updatePassword(RiotUser user, String newPassword) {
		wrappedInstance.updatePassword(user, newPassword);
		UserHolder.updateUser(user.getUserId(), user);
	}

	public Class<?> getEntityClass() {
		return wrappedInstance.getEntityClass();
	}

	public int getListSize(Object parent, ListParams params)
		throws DataAccessException {

		return wrappedInstance.getListSize(parent, params);
	}
	
	public Collection<?> list(Object parent, ListParams params) 
			throws DataAccessException {
		
		return wrappedInstance.list(parent, params);
	}
	
	public String getObjectId(Object entity) {
		return wrappedInstance.getObjectId(entity);
	}

	public Object load(String id) throws DataAccessException {		
		return wrappedInstance.load(id);
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		wrappedInstance.save(entity, parent);		
	}
	
	public Object update(Object entity) {		
		RiotUser user = (RiotUser) wrappedInstance.update((RiotUser) entity);
		UserHolder.updateUser(user.getUserId(), user);
		return user;
	}
	 
	public void delete(Object entity, Object parent) {
		wrappedInstance.delete(entity, parent);
		RiotUser user = (RiotUser) entity;
		UserHolder.updateUser(user.getUserId(), user);
	}

	public String[] getSearchableProperties() {
		if (wrappedInstance instanceof Searchable) {
			return ((Searchable) wrappedInstance).getSearchableProperties();
		}
		return null;
	}
	
}
