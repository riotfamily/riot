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

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.support.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ClientErrorLogController implements Controller {

	private Logger log = LoggerFactory.getLogger(ClientErrorLogController.class);
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String uri = ServletUtils.getRequestUri(request);
		String extension = FormatUtils.getExtension(uri);
		if (extension.equals("js")) {
			response.setContentType("text/javascript");
			ServletUtils.setFarFutureExpiresHeader(response);
			PrintWriter out = response.getWriter();
			out.print("(function(){var h=window.onerror;window.onerror=function(e,f,l){"
					+ "new Image().src='" + FormatUtils.stripExtension(uri)
					+ ".gif?error='+escape(e)+'&file='+escape(f)+'&line='+escape(l);"
					+ "if(h)return h(e,f,l)}})()");
			
			return null;
		}
		
		if (request.getParameter("error") != null) {
			log.error(String.format("Error in %s (line %s): %s",
					request.getParameter("file"),
					request.getParameter("line"),		
					request.getParameter("error")));
		}
		ServletUtils.serveTransparentPixelGif(response);
		return null;
	}

}
