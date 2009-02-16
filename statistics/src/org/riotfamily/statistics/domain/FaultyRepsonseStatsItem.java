package org.riotfamily.statistics.domain;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.util.StringUtils;

public class FaultyRepsonseStatsItem extends StatsItem {

	private String clientIp;
	
	private int status;
	
	private int count;

	private String referer;

	private String userAgent;
	
	public FaultyRepsonseStatsItem(HttpServletRequest request, int status) {
		super(request.getRequestURL().toString());
		setClientIp(request.getRemoteAddr());
		setReferer(ServletUtils.getReferer(request));
		setUserAgent(ServletUtils.getUserAgent(request));
		setStatus(status);
		count();
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
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
	
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
