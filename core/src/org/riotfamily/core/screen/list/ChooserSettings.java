package org.riotfamily.core.screen.list;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.core.screen.ScreenLink;

public class ChooserSettings implements Serializable {

	private String targetScreenId;
	
	private String startScreenId;
	
	private String rootObjectId;

	public ChooserSettings(HttpServletRequest request) {
		targetScreenId = request.getParameter("choose");
		startScreenId = request.getParameter("start");
		rootObjectId = request.getParameter("rootId");
	}

	public ScreenLink appendTo(ScreenLink link) {
		if (targetScreenId != null) {
			StringBuffer url = new StringBuffer(link.getUrl());
			ServletUtils.appendParameter(url, "choose", targetScreenId);
			ServletUtils.appendParameter(url, "start", startScreenId);
			ServletUtils.appendParameter(url, "rootId", rootObjectId);
			link.setUrl(url.toString());
		}
		return link;
	}
	
	public String getTargetScreenId() {
		return targetScreenId;
	}

	public String getStartScreenId() {
		return startScreenId;
	}

	public String getRootObjectId() {
		return rootObjectId;
	}

}
