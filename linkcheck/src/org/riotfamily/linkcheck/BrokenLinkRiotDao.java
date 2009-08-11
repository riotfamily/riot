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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.riot.hibernate.dao.HqlDao;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Transactional
public class BrokenLinkRiotDao extends HqlDao {
	
	public BrokenLinkRiotDao(SessionFactory sessionFactory) {
		super(sessionFactory);		
		setEntityClass(Link.class);
	}
	
	public String getObjectId(Object entity) {
		Link link = (Link) entity;
		return link.getSource() + '|' + link.getDestination();
	}
	
	public Object load(String id) throws DataAccessException {
		String[] s = StringUtils.split(id, "|");
		String hql = "from Link where id.source = :source and id.destination = :destination";
		Query query = getSession().createQuery(hql);
		query.setParameter("source", s[0]);
		query.setParameter("destination", s[1]);
		query.setMaxResults(1);
		return query.uniqueResult();
	}
	
}
