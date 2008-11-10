package org.riotfamily.statistics.web;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.filter.FilterPlugin;
import org.riotfamily.statistics.domain.RequestStatsItem;

public class RequestCountFilterPlugin extends FilterPlugin {

	private static RiotLog log = RiotLog.get(RequestCountFilterPlugin.class);

	private long warnThreshold;
	
	private long maxRequests;
	
	private int maxListSize = 45;
	
	private long parallelRequestsHWM;
	
	private int totalRequestCount = 0;
	
	private long totalResponseTime = 0;
	
	private String monitoredUrl;
	
	private boolean enabled = false;
	
	private boolean ignoreUploads = false;
	
	/* LinkedLists perform better than ArrayLists when modified often. */
	private LinkedList<RequestStatsItem> currentRequests = Generics.newLinkedList();

	private LinkedList<RequestStatsItem> criticalRequests = Generics.newLinkedList();

	
	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) 
			throws IOException, ServletException {

		if (!isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}

		RequestStatsItem reqStats = new RequestStatsItem(request);
		reqStats.setClientIp(request.getRemoteAddr());

		synchronized (this) {
			if (currentRequests.size() > maxRequests 
					&& monitoredUrl.equalsIgnoreCase(reqStats.getName())) {
				
				log.error("Maximum number of currentRequests reached (" 
						+ maxRequests + "). Signalling failure...");

				/* Temporarily not available */
				response.sendError(503);
				return;
			}
		}
		
		updateStatsBefore(reqStats);
		try {
			filterChain.doFilter(request, response);
		} 
		finally {
			updateStatsAfter(reqStats);
		}
	}

	private void updateStatsBefore(RequestStatsItem reqStats) {
		synchronized (this) {
			totalRequestCount++;
			currentRequests.add(reqStats);
			if (parallelRequestsHWM < currentRequests.size()) {
				parallelRequestsHWM = currentRequests.size();
			}
		}
	}

	private void updateStatsAfter(RequestStatsItem reqStats) {
		reqStats.responseDone();
		synchronized (this) {
			totalResponseTime += reqStats.getResponseTime();
			currentRequests.remove(reqStats);
			if (!ignoreUploads || !reqStats.isUpload()) {
				checkCriticalCandidate(reqStats);
			}
		}
	}

	private void checkCriticalCandidate(RequestStatsItem reqStats) {
		if (reqStats.getResponseTime() > warnThreshold) {
			if (criticalRequests.size() < maxListSize) {
				addCriticalRequest(reqStats);
			} 
			else {
				RequestStatsItem fastestReq = findFastest(criticalRequests);
				if (reqStats.getResponseTime() > fastestReq.getResponseTime()) {
					criticalRequests.remove(fastestReq);
					addCriticalRequest(reqStats);
				}
			}
			log.warn("Response time slow for URL %s (%s s)", 
					reqStats.getName(), reqStats.getResponseTime() / 1000);
		}
	}

	private void addCriticalRequest(RequestStatsItem reqStats) {
		int i = 0;
		for (RequestStatsItem item : criticalRequests) {
			if (item.getResponseTime() < reqStats.getResponseTime()) {
				criticalRequests.add(i, reqStats);
				return;
			}
		}
		criticalRequests.add(reqStats);
	}

	private RequestStatsItem findFastest(LinkedList<RequestStatsItem> list) {
		if (!list.isEmpty()) {
			return list.getFirst();
		}
		return null;
	}

	public synchronized void reset() {
		criticalRequests.clear();
		totalRequestCount = 0;
		totalResponseTime = 0;
		parallelRequestsHWM = 0;
	}

	public long getAvgResponseTime() {
		if (totalRequestCount > 0) {
			return totalResponseTime / ((long) totalRequestCount);
		}
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

	public synchronized int getCurrentRequestCount() {
		return currentRequests.size();
	}

	public int getTotalRequestCount() {
		return totalRequestCount;
	}

	public long getTotalResponseTime() {
		return totalResponseTime;
	}

	public List<RequestStatsItem> getCurrentRequests() {
		return currentRequests;
	}

	public List<RequestStatsItem> getCriticalRequests() {
		return criticalRequests;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isIgnoreUploads() {
		return ignoreUploads;
	}

	public void setIgnoreUploads(boolean ignoreUploads) {
		this.ignoreUploads = ignoreUploads;
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
