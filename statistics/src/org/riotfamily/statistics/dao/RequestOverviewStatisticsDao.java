package org.riotfamily.statistics.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import org.riotfamily.statistics.web.RequestCountFilterPlugin;

public class RequestOverviewStatisticsDao extends AbstractPropertiesDao {

	private RequestCountFilterPlugin requestCountFilterPlugin;
	
	public RequestCountFilterPlugin getRequestCountFilterPlugin() {
		return requestCountFilterPlugin;
	}
	
	public void setRequestCountFilterPlugin(RequestCountFilterPlugin requestCountFilterPlugin) {
		this.requestCountFilterPlugin = requestCountFilterPlugin;
	}
	
	protected Map getProperties() {
		Map result = new LinkedHashMap();
		result.put("Request statistics enabled", "" + requestCountFilterPlugin.isEnabled());
		result.put("Total request count", "" + requestCountFilterPlugin.getTotalRequestCount());
		result.put("Parallel request count (now)", "" + requestCountFilterPlugin.getCurrentRequestCount());
		result.put("Parallel request count (high water mark)", "" + requestCountFilterPlugin.getParallelRequestsHWM());
		if (requestCountFilterPlugin.getAvgResponseTime() >= 0) {
			result.put("Average Response time (tomcat) [ms]", "" + requestCountFilterPlugin.getAvgResponseTime());
		}
		result.put("Total Response time (tomcat) [min] ", "" + (requestCountFilterPlugin.getTotalResponseTime() / 1000 / 60));
		result.put("Parallel request count (critical threshold)", "" + requestCountFilterPlugin.getMaxRequests());
		result.put("Critical request count", "" + requestCountFilterPlugin.getCriticalRequestCount());
		return result;
	}

}
