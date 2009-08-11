package org.riotfamily.core.screen.list.service;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.core.screen.Screenlet;

public class ScreenletRenderer extends ListServiceHandler {

	RiotLog log = RiotLog.get(ScreenletRenderer.class);
	
	ScreenletRenderer(ListService service, String key,
			HttpServletRequest request) {
		
		super(service, key, request);
	}
	
	public String renderAll() {
		StringBuilder sb = new StringBuilder();
		if (screen.getScreenlets() != null) {
			for (Screenlet screenlet : screen.getScreenlets()) {
				try {
					sb.append(screenlet.render(screenContext));
				}
				catch (Exception e) {
					log.error("Error rendering Screenlet", e);
				}
			}
		}
		return sb.toString();
	}

}
