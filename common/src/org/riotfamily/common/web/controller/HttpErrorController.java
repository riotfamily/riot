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
package org.riotfamily.common.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that sends a HTTP error response with a configurable message 
 * and status code.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class HttpErrorController implements Controller {

	private int statusCode;
	
	private String message;

	public HttpErrorController(int code) {
		this.statusCode = code;
	}

	public HttpErrorController(int code, String message) {
		this.statusCode = code;
		this.message = message;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		response.sendError(statusCode, message);
		return null;
	}

}
