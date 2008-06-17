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
package org.riotfamily.components.editor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.media.model.RiotImage;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class FileUploadController implements Controller {

	private String fileParam = "Filedata";
	
	private String tokenParameter = "token";
	
	private UploadManager uploadManager;
	
	public FileUploadController(UploadManager uploadManager) {
		this.uploadManager = uploadManager;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (request instanceof MultipartHttpServletRequest) {
			handleMultipartRequest((MultipartHttpServletRequest) request, response);
		}
		else {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		return null;
	}
	
	protected void handleMultipartRequest(MultipartHttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String token = request.getParameter(tokenParameter);
		if (uploadManager.isValidToken(token)) {
			MultipartFile multipartFile = request.getFile(fileParam);
			if ((multipartFile != null) && (!multipartFile.isEmpty())) {
				RiotImage image = uploadManager.storeImage(token, multipartFile);
				PrintWriter out = response.getWriter();
				out.print("({id: ");
				out.print(image.getId());
				out.print(", uri: '");
				out.print(image.getUri());
				out.print("'})");
				out.flush();
			}
		}
		else {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			response.getWriter().print("Invalid token");
		}
	}
	
}
