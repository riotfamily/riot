package org.riotfamily.forms.element;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.Element;

/**
 * Interface that may be implemented by elements that need to provide 
 * extra content to the client within a separate request. For instance it
 * can be used to serve the content of an iframe, image or plugin tag.
 */
public interface ContentElement extends Element {

	public void handleContentRequest(HttpServletRequest request, 
			HttpServletResponse response) throws IOException;
}
