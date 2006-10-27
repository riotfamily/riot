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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/**
 * MultipartResolver implementation to track the progress of file uploads.
 * Acts as wrapper so that the actual work is performed by the implementation
 * passed as constructor argument. Instead of the original request a
 * {@link org.riotfamily.forms.fileupload.HttpUploadRequest HttpUploadRequest} is
 * passed to the underlying resolver which provides the progress information.
 */
public class FormsMultipartResolver implements MultipartResolver {

	private static final String UPLOAD_ID_ATTR = 
			FormsMultipartResolver.class.getName() + ".uploadId";
	
	private Log log = LogFactory.getLog(FormsMultipartResolver.class);
	
	private MultipartResolver resolver;
	
	private String uploadIdParam = "uploadId";
	
	public FormsMultipartResolver(MultipartResolver resolver) {
		this.resolver = resolver;
	}
	
	public boolean isMultipart(HttpServletRequest request) {
		return resolver.isMultipart(request);
	}

	public MultipartHttpServletRequest resolveMultipart(
			HttpServletRequest request) throws MultipartException {
		
		String uploadId = request.getParameter(uploadIdParam);
		if (uploadId == null) {
			log.debug("Parameter '" + uploadIdParam + "' not set");
			return resolver.resolveMultipart(request);
		}
		log.debug("Upload: " + uploadId);
		HttpUploadRequest uploadRequest = new HttpUploadRequest(request);
		request.setAttribute(UPLOAD_ID_ATTR, uploadId);
		UploadStatus.add(uploadId, uploadRequest);		
		return resolver.resolveMultipart(uploadRequest);
	}

	public void cleanupMultipart(MultipartHttpServletRequest request) {
		UploadStatus.clearStatus(request.getAttribute(UPLOAD_ID_ATTR));
		resolver.cleanupMultipart(request);
	}

}
