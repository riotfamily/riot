package org.riotfamily.cachius.http.content;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IncludeDirective implements Directive {

	private Pattern pattern = Pattern.compile("^\\s*include\\s+(.+)\\s*$");
	
	public ContentFragment parse(String source) {
		Matcher m = pattern.matcher(source);
		if (m.matches()) {
			return new IncludeFragment(m.group(1));
		}
		return null;
	}
	
	private static class IncludeFragment implements ContentFragment {
		
		private String url;
	
		public IncludeFragment(String url) {
			this.url = url;
		}

		public int getLength(HttpServletRequest request,
				HttpServletResponse response) {
	
			return -1;
		}
		
		public void serve(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			
			request.getRequestDispatcher(url).include(request, response);
			
		}
	}
	
}
