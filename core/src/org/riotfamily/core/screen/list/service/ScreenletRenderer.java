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
