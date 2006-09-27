package org.riotfamily.riot.list.command.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

public class SetRowStyleResult implements CommandResult {

	private String objectId;
	
	private String rowStyle;
	
	
	public SetRowStyleResult(String objectId, String rowStyle) {
		this.objectId = objectId;
		this.rowStyle = rowStyle;
	}

	public String getJavaScriptCode(HttpServletRequest request, 
			HttpServletResponse response) {
		
		StringBuffer js = new StringBuffer();
		js.append("Element.addClassName('object-");
		js.append(objectId);
		js.append("', '");
		js.append(rowStyle);
		js.append("');");
		return js.toString();
	}
	

}
