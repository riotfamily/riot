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
package org.riotfamily.statistics.domain;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.support.ServletUtils;

public class RequestStatsItem extends StatsItem {

	private Date timestamp = new Date();

	private long responseTime = -1;

	private String clientIp;
	
	private boolean upload;

	public RequestStatsItem(HttpServletRequest request) {
		super(request.getRequestURI());
		this.clientIp = request.getRemoteAddr();
		this.upload = ServletUtils.isMultipartRequest(request);
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public long getResponseTime() {
		if (responseTime == -1 && timestamp != null) {
			return System.currentTimeMillis() - timestamp.getTime();
		}
		return responseTime;
	}

	public void responseDone() {
		responseTime = System.currentTimeMillis() - timestamp.getTime();
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public boolean isUpload() {
		return upload;
	}
}
