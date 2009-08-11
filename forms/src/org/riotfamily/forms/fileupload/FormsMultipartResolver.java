package org.riotfamily.forms.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.RiotLog;
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

	private RiotLog log = RiotLog.get(FormsMultipartResolver.class);
	
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
