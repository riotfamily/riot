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
