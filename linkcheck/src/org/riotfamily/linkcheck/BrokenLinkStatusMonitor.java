package org.riotfamily.linkcheck;

import org.riotfamily.riot.status.AbstractStatusMonitor;

public class BrokenLinkStatusMonitor extends AbstractStatusMonitor {

	public static final String DEFAULT_MESSAGE_KEY = "status.brokenLinks";
	
	private LinkDao linkDao;
	
	public BrokenLinkStatusMonitor(LinkDao linkDao) {
		this.linkDao = linkDao;
		setHideZeroStatus(false);
		setMessageKey(DEFAULT_MESSAGE_KEY);
	}

	protected Object[] getArgs() {
		return new Object[] { new Integer(linkDao.countBrokenLinks()) };
	}

}
