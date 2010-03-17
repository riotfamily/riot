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
package org.riotfamily.common.web.mvc.multipart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class ProgressMonitoringMultipartResolver extends CommonsMultipartResolver {

	private String uploadIdParamName = "uploadId";
	
	public void setUploadIdParamName(String uploadIdParamName) {
		this.uploadIdParamName = uploadIdParamName;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);
		String uploadId = request.getParameter(uploadIdParamName);
		if (uploadId != null) {
			fileUpload.setProgressListener(new UploadProgressListener(ProgressMonitor.newProgress(uploadId, request.getContentLength())));
		}
		try {
			List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
			return parseFileItems(fileItems, encoding);
		}
		catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
		}
		catch (FileUploadException ex) {
			throw new MultipartException("Could not parse multipart servlet request", ex);
		}
	}

	/**
	 * Overrides the default implementation to create a new FileUpload instance
	 * for each request.
	 */
	@Override
	protected FileUpload prepareFileUpload(String encoding) {
		FileUpload fileUpload = newFileUpload(getFileItemFactory());
		fileUpload.setSizeMax(getFileUpload().getSizeMax());
		fileUpload.setHeaderEncoding(encoding);
		return fileUpload;
	}
	
	protected static class UploadProgressListener implements ProgressListener {

		private UploadProgress progress;
		
		public UploadProgressListener(UploadProgress progress) {
			this.progress = progress;
		}

		public void update(long bytesRead, long contentLength, int currentField) {
			progress.update(bytesRead);
			
		}
		
	}
	
}
