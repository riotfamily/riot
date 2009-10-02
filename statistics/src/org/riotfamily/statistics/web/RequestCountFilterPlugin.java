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
package org.riotfamily.statistics.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.filter.FilterPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.riotfamily.statistics.domain.FaultyRepsonseStatsItem;
import org.riotfamily.statistics.domain.RequestStatsItem;

public class RequestCountFilterPlugin extends FilterPlugin {

	private static Logger log = LoggerFactory.getLogger(RequestCountFilterPlugin.class);

	private long warnThreshold;
	
	private long maxRequests;
	
	private int maxListSize = 45;
	
	private long parallelRequestsHWM;
	
	private int totalRequestCount = 0;

	private int faultyResonseCount = 0;

	private long totalResponseTime = 0;
	
	private String monitoredUrl;
	
	private boolean enabled = false;
	
	private boolean ignoreUploads = false;
	
	/* LinkedLists perform better than ArrayLists when modified often. */
	private LinkedList<RequestStatsItem> currentRequests = Generics.newLinkedList();

	private LinkedList<RequestStatsItem> criticalRequests = Generics.newLinkedList();

	private LinkedList<FaultyRepsonseStatsItem> faultyResponses = Generics.newLinkedList();

	private List<Integer> faultStatusCodes = Collections.singletonList(
			HttpServletResponse.SC_NOT_FOUND);
	
	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) 
			throws IOException, ServletException {

		if (!isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}

		RequestStatsItem reqStats = new RequestStatsItem(request);

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
		StatusResponseWrapper statusResponse = new StatusResponseWrapper(response);
		try {
			filterChain.doFilter(request, statusResponse);
		} 
		finally {
			updateStatsAfter(reqStats);
			checkFaultyResponse(request, statusResponse);			
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
			log.warn("Response time slow for URL {} ({} s)", 
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

	private void checkFaultyResponse(HttpServletRequest request,
			StatusResponseWrapper response) {
		
		if (faultStatusCodes.contains(response.getStatus())) {
			synchronized (this) {
				faultyResonseCount++;
				addFaultyResponse(
						new FaultyRepsonseStatsItem(request, response.getStatus()));
			}
		}
	}

	private void addFaultyResponse(FaultyRepsonseStatsItem reqStats) {
		for (FaultyRepsonseStatsItem item : faultyResponses) {
			if (item.getName().equals(reqStats.getName())
					&& item.getStatus() == reqStats.getStatus()) {
				
				item.count();
				faultyResponses.remove(item);
				faultyResponses.add(item);
				return;
			}
		}
		faultyResponses.add(reqStats);
		if (faultyResponses.size() > maxListSize) {
			faultyResponses.removeFirst();
		}
	}
	
	public synchronized void reset() {
		criticalRequests.clear();
		faultyResponses.clear();
		totalRequestCount = 0;
		faultyResonseCount = 0;
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

	public int getFaultyResponseCount() {
		return faultyResonseCount;
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

	public LinkedList<FaultyRepsonseStatsItem> getFaultyResponses() {
		return faultyResponses;
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

	public void setFaultStatusCodes(String statusCodes) {
		String[] codeSplit = statusCodes.split(",");
		this.faultStatusCodes = new ArrayList<Integer>(codeSplit.length); 
		for (String code : codeSplit) {
			this.faultStatusCodes.add(Integer.valueOf(code));
		}
	}

	private class StatusResponseWrapper extends HttpServletResponseWrapper {

		private int status = HttpServletResponse.SC_OK;
		
		public StatusResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		public void	sendError(int sc) throws IOException {
			super.sendError(sc);
			setStatusInternal(sc);
		}

		public void	sendError(int sc, String msg) throws IOException {
			super.sendError(sc, msg);
			setStatusInternal(sc);
		}
		
		public void setStatus(int sc) {
			super.setStatus(sc);
			setStatusInternal(sc);
		}
		
		public void setStatus(int sc, String sm) {
			super.setStatus(sc, sm);
			setStatusInternal(sc);
		}
		
		private void setStatusInternal(int status) {
			this.status = status;
		}
		
		public int getStatus() {
			return status;
		}
		 
	}
	
}
