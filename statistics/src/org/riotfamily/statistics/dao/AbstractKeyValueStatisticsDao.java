package org.riotfamily.statistics.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.statistics.common.KeyValueStatisticsComparator;
import org.riotfamily.statistics.domain.SimpleStatistics;
import org.springframework.dao.DataAccessException;

public abstract class AbstractKeyValueStatisticsDao implements RiotDao {

	protected abstract List listInternal(Object parent, ListParams params);

	public void delete(Object entity, Object parent) throws DataAccessException {
	}

	public Class getEntityClass() {
		return SimpleStatistics.class;
	}

	public int getListSize(Object parent, ListParams params) throws DataAccessException {
		return listInternal(parent, params).size();
	}


	public String getObjectId(Object entity) {
		return ((SimpleStatistics)entity).getName();
	}

	public Collection list(Object parent, ListParams params) throws DataAccessException {
		return getSortedPage(listInternal(parent, params), params);
	}

	public Object load(String id) throws DataAccessException {
		return null;
	}

	public void reattach(Object entity) throws DataAccessException {
	}

	public void save(Object entity, Object parent) throws DataAccessException {
	}

	public void update(Object entity) throws DataAccessException {
	}

	protected boolean isFiltered(ListParams params, String key, String value) {
		boolean result = true;
		if (params != null && params.getSearch() != null) {
			result = (key.contains(params.getSearch()) || value.contains(params.getSearch()));
		}
		return result;
	}

	private Collection getSortedPage(List completeList, ListParams params ) {

		if (params.getOffset() >= completeList.size()) {
			return new ArrayList();
		} else { 
			if (params.getOrder() != null && params.getOrder().size() > 0 ) {
				Order order = (Order)params.getOrder().get(0);
				Collections.sort(completeList, new KeyValueStatisticsComparator(order));
			}
			int toIdx = Math.min(completeList.size(), params.getOffset() + params.getPageSize());
			return completeList.subList(params.getOffset(), toIdx);
		}
	}
}
