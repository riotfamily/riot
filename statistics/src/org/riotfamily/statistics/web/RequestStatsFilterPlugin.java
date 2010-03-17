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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.riotfamily.common.web.filter.FilterPlugin;
import org.riotfamily.statistics.domain.RequestStatsItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestStatsFilterPlugin implements FilterPlugin {

	private Logger log = LoggerFactory.getLogger(RequestStatsFilterPlugin.class);
	
	private RequestStats stats;
	
	public RequestStatsFilterPlugin(RequestStats stats) {
		this.stats = stats;
	}

	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) 
			throws IOException, ServletException {

		if (!stats.isEnabled()) {
			filterChain.doFilter(request, response);
			return;
		}

		RequestStatsItem item = new RequestStatsItem(request);

		synchronized (this) {
			if (stats.signalFailure(item.getName())) {
				log.error("Maximum number of currentRequests reached ({}). Signalling failure...", stats.getMaxRequests());
				response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				return;
			}
		}
		
		stats.updateStatsBefore(item);
		StatusResponseWrapper statusResponse = new StatusResponseWrapper(response);
		try {
			filterChain.doFilter(request, statusResponse);
		} 
		finally {
			stats.updateStatsAfter(item);
			stats.checkFaultyResponse(request, statusResponse.getStatus());			
		}
	}
	
	private static class StatusResponseWrapper extends HttpServletResponseWrapper {

		private int status = HttpServletResponse.SC_OK;
		
		public StatusResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void	sendError(int sc) throws IOException {
			super.sendError(sc);
			setStatusInternal(sc);
		}

		@Override
		public void	sendError(int sc, String msg) throws IOException {
			super.sendError(sc, msg);
			setStatusInternal(sc);
		}
		
		@Override
		public void setStatus(int sc) {
			super.setStatus(sc);
			setStatusInternal(sc);
		}
		
		@Override
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
