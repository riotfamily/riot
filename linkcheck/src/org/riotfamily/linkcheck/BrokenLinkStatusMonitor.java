package org.riotfamily.linkcheck;

import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.status.I18nStatusMonitor;

public class BrokenLinkStatusMonitor extends I18nStatusMonitor {

	public BrokenLinkStatusMonitor() {
		setMessageKey("status.brokenLinks");
	}

	protected Object getArg(ScreenContext context) {
		return Link.countBrokenLinks();
	}

}
