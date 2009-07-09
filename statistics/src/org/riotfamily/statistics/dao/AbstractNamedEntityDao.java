package org.riotfamily.statistics.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.Order;
import org.riotfamily.riot.dao.support.RiotDaoAdapter;
import org.riotfamily.statistics.common.NamedEntityComparator;
import org.riotfamily.statistics.domain.NamedEntity;
import org.springframework.dao.DataAccessException;

public abstract class AbstractNamedEntityDao extends RiotDaoAdapter {

	protected abstract List listInternal(Object parent, ListParams params);

	public Class getEntityClass() {
		return NamedEntity.class;
	}

	public int getListSize(Object parent, ListParams params) throws DataAccessException {
		return listInternal(parent, params).size();
	}


	public String getObjectId(Object entity) {
		return ((NamedEntity)entity).getName ();
	}

	public Collection list(Object parent, ListParams params) throws DataAccessException {
		return getSortedPage(listInternal(parent, params), params);
	}

	protected boolean isFiltered(ListParams params, String name) {
		boolean result = true;
		if (params != null && params.getSearch() != null) {
			result = name.contains(params.getSearch()) ;
		}
		return result;
	}

	private Collection getSortedPage(List completeList, ListParams params ) {

		if (params.getOffset() >= completeList.size()) {
			return new ArrayList();
		} else { 
			if (params.getOrder() != null && params.getOrder().size() > 0 ) {
				Order order = (Order)params.getOrder().get(0);
				Collections.sort(completeList, new NamedEntityComparator(order));
			}
			int toIdx = Math.min(completeList.size(), params.getOffset() + params.getPageSize());
			return completeList.subList(params.getOffset(), toIdx);
		}
	}
}
