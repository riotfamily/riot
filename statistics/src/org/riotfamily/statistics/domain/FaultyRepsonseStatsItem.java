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
import org.springframework.util.StringUtils;

public class FaultyRepsonseStatsItem extends StatsItem {

	private int status;

	private int count;

	private Date lastTime;

	private String referer;
	
	private String clientIp;

	private String userAgent;
	
	public FaultyRepsonseStatsItem(HttpServletRequest request, int status) {
		super(request.getRequestURL().toString());
		setStatus(status);
		setReferer(ServletUtils.getReferer(request));
		setClientIp(request.getRemoteAddr());
		setUserAgent(ServletUtils.getUserAgent(request));
		count();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void count() {
		this.count++;
		setLastTime(new Date());
	}

	public Date getLastTime() {
		return lastTime;
	}
	
	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}
	
	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public boolean hasReferer() {
		return StringUtils.hasText(referer);
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
