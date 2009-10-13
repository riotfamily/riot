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
package org.riotfamily.forms.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger log = LoggerFactory.getLogger(FormsMultipartResolver.class);
	
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
		UploadStatus status = UploadStatus.add(uploadId, uploadRequest);
		try {
			return resolver.resolveMultipart(uploadRequest);
		}
		catch (MultipartException e) {
			status.setException(e);
			throw e;
		}
	}

	public void cleanupMultipart(MultipartHttpServletRequest request) {
		resolver.cleanupMultipart(request);
	}

}
