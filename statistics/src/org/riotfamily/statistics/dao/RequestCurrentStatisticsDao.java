package org.riotfamily.statistics.dao;

import java.util.ArrayList;
import java.util.List;

import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class RequestCurrentStatisticsDao extends AbstractNamedEntityDao {

	private RequestCountFilterPlugin requestCountFilterPlugin;
	
	public RequestCountFilterPlugin getRequestCountFilterPlugin() {
		return requestCountFilterPlugin;
	}
	
	public void setRequestCountFilterPlugin(RequestCountFilterPlugin requestCountFilterPlugin) {
		this.requestCountFilterPlugin = requestCountFilterPlugin;
	}

	protected List listInternal(Object parent, ListParams params) {
		return new ArrayList(requestCountFilterPlugin.getCurrentRequests());
	}
}
