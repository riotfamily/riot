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
package org.riotfamily.statistics.dao;

import java.util.Collection;
import java.util.List;

import org.riotfamily.core.dao.InMemoryRiotDao;
import org.riotfamily.statistics.domain.StatsItem;
import org.springframework.dao.DataAccessException;

public abstract class AbstractStatsItemDao extends InMemoryRiotDao {

	@Override
	public Class<?> getEntityClass() {
		return StatsItem.class;
	}

	@Override
	public String getObjectId(Object entity) {
		return ((StatsItem) entity).getName();
	}
	
	@Override
	public Object load(String id) throws DataAccessException {
		return null;
	}

	@Override
	public final Collection<?> listInternal(Object parent) throws Exception {
		return getStats();
	}

	@Override
	public boolean canSortBy(String property) {
		return false;
	}
	
	protected abstract List<? extends StatsItem> getStats() throws Exception;

}
