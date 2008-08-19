package org.riotfamily.statistics.domain;

import java.util.Date;

public class RequestStatsItem extends StatsItem {

	private Date timestamp = new Date();

	private long responseTime = -1;

	private String clientIp;

	public RequestStatsItem(String name) {
		super(name);
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

}
