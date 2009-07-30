package org.riotfamily.statistics.domain;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;

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
