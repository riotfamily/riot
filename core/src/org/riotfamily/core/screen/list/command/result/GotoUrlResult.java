package org.riotfamily.core.screen.list.command.result;

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteMethod;
import org.riotfamily.core.screen.list.command.CommandResult;

@DataTransferObject
public class GotoUrlResult implements CommandResult {

	private String url;
	
	private String target = "self";
	
	private boolean replace;
	
	public GotoUrlResult(String url) {
		this.url = url;
	}
	
	public GotoUrlResult(HttpServletRequest request, String url) {
		this.url = request.getContextPath() + url;
	}
	
	@RemoteMethod
	public String getAction() {
		return "gotoUrl";
	}

	@RemoteMethod
	public boolean isReplace() {
		return this.replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	@RemoteMethod
	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@RemoteMethod
	public String getUrl() {
		return this.url;
	}

}
