package org.riotfamily.riot.list.command.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

public class GotoUrlResult implements CommandResult {

	private String url;
	
	private String target = "window";
	
	private boolean replace;
	
	private boolean contextRelative;

	public GotoUrlResult(String url) {
		this(url, true);
	}
	
	public GotoUrlResult(String url, boolean contextRelative) {
		this.url = url;
		this.contextRelative = contextRelative;
	}

	public String getJavaScriptCode(HttpServletRequest request, 
			HttpServletResponse response) {
		
		StringBuffer js = new StringBuffer();
		js.append(target);
		js.append(".location.");
		js.append(replace ? "replace('" : "href = '");
		String href = contextRelative ? request.getContextPath() + url : url;  
		js.append(response.encodeURL(href));
		js.append('\'');
		if (replace) {
			js.append(')');
		}
		return js.toString();
	}
}
