package org.riotfamily.riot.list.command.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

public class ReloadResult implements CommandResult {

	private static final String GET_WITHOUT_PARAMS = 
			"var url = window.location.href;\n" +
			"var i = url.indexOf('?');\n" +
			"if (i != -1) url = url.substring(0, i);\n" +
			"window.location.replace(url);";
	
	private static final String RELOAD = "window.location.reload()";
	
	private boolean getWithoutParams;
	
	public ReloadResult() {
		this(true);
	}
	
	public ReloadResult(boolean getWithoutParams) {
		this.getWithoutParams = getWithoutParams;
	}
	
	public String getJavaScriptCode(HttpServletRequest request, 
			HttpServletResponse response) {
		
		return getWithoutParams ? GET_WITHOUT_PARAMS : RELOAD;
	}
}
