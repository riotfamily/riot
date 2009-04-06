/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.screen.list;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.core.screen.ScreenLink;

public class ChooserSettings {

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
