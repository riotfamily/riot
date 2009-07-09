package org.riotfamily.statistics.web;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.filter.FilterPlugin;
import org.riotfamily.common.web.filter.PluginChain;
import org.riotfamily.statistics.domain.RequestStatistics;
import org.springframework.core.CollectionFactory;

public class RequestCountFilterPlugin extends FilterPlugin {

	private static final String TOP_LEVEL_REQUEST_STAMP = "TOP_LEVEL_REQUEST_STAMP";

	private static Log log = LogFactory.getLog(RequestCountFilterPlugin.class);

	private long warnThreshold;
	private long maxRequests;
	private int maxListSize = 45;
	private long parallelRequestsHWM;
	private int totalRequestCount = 0;
	private long totalResponseTime = 0;
	private String monitoredUrl;
	private boolean enabled = false;
	CollectionFactory s;
	
	/* LinkedLists perform better than ArrayLists when modified often. */
	private List currentRequests = new LinkedList();
	private List criticalRequests = new LinkedList();

	public void doFilter(HttpServletRequest request, HttpServletResponse response, PluginChain pluginChain) throws IOException,
			ServletException {

		if (!isEnabled() || request.getAttribute(TOP_LEVEL_REQUEST_STAMP) != null) {
			pluginChain.doFilter(request, response);
			return;
		}

		request.setAttribute(TOP_LEVEL_REQUEST_STAMP, new Long(System.currentTimeMillis()));
		String url = request.getRequestURI();

		RequestStatistics reqStats = new RequestStatistics(url);
		reqStats.setClientIp(request.getRemoteAddr());

		synchronized (this) {
			if (currentRequests.size() > maxRequests && monitoredUrl.equalsIgnoreCase(url)) {
				log.error("Maximum number of currentRequests reached (" + maxRequests + "). Signalling failure...");

				/* Temporarily not available */
				response.sendError(503);
				return;
			}
		}
		
		updateStatsBefore(reqStats);
		try {
			pluginChain.doFilter(request, response);
		} finally {
			updateStatsAfter(reqStats);
		}
	}

	private void updateStatsBefore(RequestStatistics reqStats) {
		synchronized (this) {
			totalRequestCount++;
			currentRequests.add(reqStats);
			if (parallelRequestsHWM < currentRequests.size()) {
				parallelRequestsHWM = currentRequests.size();
			}
		}
	}

	private void updateStatsAfter(RequestStatistics reqStats) {
		reqStats.responseDone();
		synchronized (this) {
			totalResponseTime += reqStats.getResponseTime();
			currentRequests.remove(reqStats);
			checkCriticalCandidate(reqStats);
		}
	}

	private void checkCriticalCandidate(RequestStatistics reqStats) {
		if (reqStats.getResponseTime() > warnThreshold) {
			if (criticalRequests.size() < maxListSize) {
				addCriticalRequest(reqStats);
			} else {
				RequestStatistics fastestReq = findFastest(criticalRequests);
				if (reqStats.getResponseTime() > fastestReq.getResponseTime()) {
					criticalRequests.remove(fastestReq);
					addCriticalRequest(reqStats);
				}
			}
			if (log.isWarnEnabled()) {
				log.warn("Response time slow for URL " + reqStats.getName() + " ("
						+ (reqStats.getResponseTime() / 1000) + " s)");
			}
		}
	}

	private void addCriticalRequest(RequestStatistics reqStats) {
		for (int i = 0; i < criticalRequests.size(); i++) {
			RequestStatistics rs = (RequestStatistics)criticalRequests.get(i);
			if (rs.getResponseTime() > reqStats.getResponseTime() ) {
				criticalRequests.add(i, reqStats);
				return;
			}
		}
		criticalRequests.add(reqStats);
	}

	private RequestStatistics findFastest(List list) {
		RequestStatistics result = null;
		if (list.size() > 0) {
			result = (RequestStatistics)list.get(0);
		}
		return result;
	}

	public void reset() {
		synchronized (this) {
			criticalRequests.clear();
			totalRequestCount = 0;
			totalResponseTime = 0;
			parallelRequestsHWM = 0;
		}
	}

	public long getAvgResponseTime() {
		if (totalRequestCount > 0)
			return totalResponseTime / ((long) totalRequestCount);
		else
			return -1;
	}

	public long getWarnThreshold() {
		return warnThreshold;
	}

	public void setWarnThreshold(long warnThreshold) {
		this.warnThreshold = warnThreshold;
	}

	public long getMaxRequests() {
		return maxRequests;
	}

	public void setMaxRequests(long maxRequests) {
		this.maxRequests = maxRequests;
	}

	public String getMonitoredUrl() {
		return monitoredUrl;
	}

	public void setMonitoredUrl(String monitoredUrl) {
		this.monitoredUrl = monitoredUrl;
	}

	public int getCurrentRequestCount() {
		synchronized (this) {
			return currentRequests.size();
		}
	}

	public int getTotalRequestCount() {
		return totalRequestCount;
	}

	public long getTotalResponseTime() {
		return totalResponseTime;
	}

	public List getCurrentRequests() {
		return currentRequests;
	}

	public List getCriticalRequests() {
		return criticalRequests;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getParallelRequestsHWM() {
		return parallelRequestsHWM;
	}

	public long getCriticalRequestCount() {
		synchronized (this) {
			return criticalRequests.size();
		}
	}

}
