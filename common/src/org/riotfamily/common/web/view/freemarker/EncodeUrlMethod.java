package org.riotfamily.common.web.view.freemarker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;

public class EncodeUrlMethod extends AbstractStringTransformerMethod {

	protected String transform(String s, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		return ServletUtils.resolveAndEncodeUrl(s, request, response);
	}

}
