package org.riotfamily.statistics.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.statistics.domain.SimpleStatistics;
import org.springframework.dao.DataAccessException;

public abstract class AbstractPropertiesDao extends AbstractKeyValueStatisticsDao {

	protected List listInternal(Object entity, ListParams params) throws DataAccessException {
		List list = new ArrayList();
		Map props = getProperties();
		int idx = 0;
		for (Iterator iterator = props.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = (String)props.get(key);
			if (isFiltered (params, key, value)) {
				SimpleStatistics entry = new SimpleStatistics();
				entry.setIdx(++idx);
				entry.setName(key);
				entry.setValue(value);
				list.add(entry); 
			}
		}
		return list;

	}
	

	public Object load(String id) throws DataAccessException {
		String value = System.getProperties().getProperty(id);
		return new SimpleStatistics(id, value);
	}
	
	protected abstract Map getProperties();

}
