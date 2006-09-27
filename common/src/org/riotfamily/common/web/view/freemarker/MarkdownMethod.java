package org.riotfamily.common.web.view.freemarker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.markdown.MarkdownProcessor;
import org.riotfamily.common.markup.markdown.QuoteStyle;

public class MarkdownMethod extends AbstractStringTransformerMethod {
	
	protected String transform(String s, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		if (s == null) {
    		return null;
    	}
		MarkdownProcessor markdown = new MarkdownProcessor(request, response);
		//TODO Make QuoteStyle configurable or use the request locale to determine it automatically 
		markdown.setQuoteStyle(QuoteStyle.DE);
		return markdown.markdown(s);
	}

}
