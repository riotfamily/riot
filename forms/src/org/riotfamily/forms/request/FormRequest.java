package org.riotfamily.forms.request;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public interface FormRequest {

	public String getParameter(String name);

	public String[] getParameterValues(String name);
	
	public MultipartFile getFile(String name);

}
