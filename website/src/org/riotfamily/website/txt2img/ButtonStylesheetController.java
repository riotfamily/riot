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
package org.riotfamily.website.txt2img;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.website.cache.controller.CacheableController;
import org.riotfamily.website.cache.controller.Compressible;
import org.springframework.web.servlet.ModelAndView;

public class ButtonStylesheetController implements CacheableController, Compressible {

	private ButtonService buttonService;
	
	public ButtonStylesheetController(ButtonService buttonService) {
		this.buttonService = buttonService;
	}

	public boolean gzipResponse(HttpServletRequest request) {
		return true;
	}
	
	public String getCacheKey(HttpServletRequest request) {
		return "txt2imgButtonRules";
	}

	public long getTimeToLive() {
		return buttonService.isReloadable() ? 0 : CACHE_ETERNALLY;
	}
	
	public long getLastModified(HttpServletRequest request) {
		return buttonService.getLastModified();
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/css");
		buttonService.writeRules(response.getWriter());
		return null;
	}

}
