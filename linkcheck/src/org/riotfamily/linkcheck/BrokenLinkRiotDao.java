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
package org.riotfamily.linkcheck;

import org.hibernate.SessionFactory;
import org.riotfamily.core.dao.hibernate.HqlDao;
import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

public class BrokenLinkRiotDao extends HqlDao {

	public BrokenLinkRiotDao(SessionFactory sessionFactory) {
		super(sessionFactory);
		setEntityClass(BrokenLink.class);
	}
	
	@Override
	public String getObjectId(Object entity) {
		BrokenLink brokenLink = (BrokenLink) entity;
		return brokenLink.getSource() + '|' + brokenLink.getDestination();
	}
	
	@Override
	public Object load(String id) throws DataAccessException {
		String[] s = StringUtils.split(id, "|");
		return BrokenLink.load(s[0], s[1]);
	}
	
	/*
	@Override
	public int getListSize(Object parent, ListParams params) throws DataAccessException {
		return BrokenLink.countBrokenLinks();
	}

	public String getObjectId(Object entity) {
		BrokenLink link = (BrokenLink) entity;
		return link.getSource() + '|' + link.getDestination();
	}

	@Override
	public Collection<?> list(Object parent, ListParams params) throws DataAccessException {
		return BrokenLink.findAllBrokenLinks();
	}

	public Object load(String id) throws DataAccessException {
		String[] s = StringUtils.split(id, "|");
		return BrokenLink.load(s[0], s[1]);
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		((BrokenLink) entity).save();
	}

	public Object update(Object entity) throws DataAccessException {
		return ((BrokenLink) entity).merge();
	}

	public void delete(Object entity, Object parent) throws DataAccessException {
		((BrokenLink) entity).delete();
	}
	*/
	
}
