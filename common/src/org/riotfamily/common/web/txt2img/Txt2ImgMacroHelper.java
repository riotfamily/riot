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
package org.riotfamily.common.web.txt2img;

import javax.servlet.http.HttpServletRequest;

public class Txt2ImgMacroHelper {

	private ButtonService buttonService;
	
	private HttpServletRequest request;
	
	
	public Txt2ImgMacroHelper(ButtonService buttonService,
			HttpServletRequest request) {
		
		this.buttonService = buttonService;
		this.request = request;
	}

	public String getButtonClass(String id) throws Exception {
		if (buttonService.hasAlpha(id)) {
			return id + " txt2imgbtn-alpha";
		}
		return id;
	}
	
	public String getButtonStyle(String id, String label) throws Exception {
		return buttonService.getInlineStyle(id, label, request);
	}

}
