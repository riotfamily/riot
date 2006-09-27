package org.riotfamily.common.web.view.freemarker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.textile.Textile;

public class TextileMethod extends AbstractStringTransformerMethod {
	
	protected String transform(String s, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (s == null) {
    		return null;
    	}
		Textile textile = new Textile(request, response);
		return textile.textileThis(s);
	}

}
