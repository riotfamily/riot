package org.riotfamily.forms.request;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class HttpFormRequest implements FormRequest {

	private HttpServletRequest request;
	
	public HttpFormRequest(HttpServletRequest request) {
		this.request = request;
	}

	public MultipartFile getFile(String name) {
		if (request instanceof MultipartHttpServletRequest) {
			return ((MultipartHttpServletRequest) request).getFile(name);
		}
		return null;
	}

	public String getParameter(String name) {
		return request.getParameter(name);
	}

	public String[] getParameterValues(String name) {
		return request.getParameterValues(name);
	}

}
