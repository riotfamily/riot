package org.riotfamily.riot.list.command.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

public class PopupResult implements CommandResult {

	private String url;
	
	private String windowName;
	
	public PopupResult(String url) {
		this.url = url;
	}

	public String getJavaScriptCode(HttpServletRequest request, 
			HttpServletResponse response) {
		
		StringBuffer js = new StringBuffer();
		js.append("window.open('");
		js.append(url).append('\'');
		if (windowName != null) {
			js.append(", '");
			js.append(windowName);
			js.append('\'');
		}
		js.append(')');
		return js.toString();
	}
}
