package org.riotfamily.statistics.dao;

import java.util.Collection;
import java.util.List;

import org.riotfamily.core.dao.InMemoryRiotDao;
import org.riotfamily.statistics.domain.StatsItem;

public abstract class AbstractStatsItemDao extends InMemoryRiotDao {

	public Class<?> getEntityClass() {
		return StatsItem.class;
	}

	public String getObjectId(Object entity) {
		return ((StatsItem) entity).getName();
	}

	@Override
	protected final Collection<?> listInternal(Object parent) throws Exception {
		return getStats();
	}

	protected abstract List<? extends StatsItem> getStats() throws Exception;

}
