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
package org.riotfamily.media.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.io.IOUtils;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.media.store.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class DownloadController implements Controller {

	private Logger log = LoggerFactory.getLogger(DownloadController.class);
	
	private	FileStore fileStore;
	
	private Pattern refererPattern;	
	
	public DownloadController(FileStore fileStore) {
		this.fileStore = fileStore;
	}
	
	/**
	 * Sets a regular expression that is used to check the Referer header.
	 * You may use this setting to prevent other websites from using your
	 * DownloadController.
	 */
	public void setRefererPattern(Pattern refererPattern) {
		this.refererPattern = refererPattern;
	}	
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (!validReferer(request)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}		
		String uri = "/" + HandlerUrlUtils.getPathWithinMapping(request);
		if (uri != null) {
			File file = fileStore.retrieve(uri);
			log.debug("Serving file "+uri+" with content-disposition: attachment");
			if (file != null && file.canRead()) {
				response.setHeader("Content-Disposition", "attachment");
				response.setContentLength((int) file.length());
				IOUtils.serve(new FileInputStream(file), 
						response.getOutputStream());

				return null;
			}
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return null;
	}
	
	private boolean validReferer(HttpServletRequest request) {
		if (refererPattern == null) {
			return true;
		}
		String referer = request.getHeader("Referer");
		if (referer != null && !refererPattern.matcher(referer).matches()) {
			log.debug("Invalid request from referer: " + referer);
			return false;
		}
		return true;
	}	

}
