package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.CommandResult;


@DataTransferObject
public class PopupResult implements CommandResult {

	private String url;
	
	private String windowName;
	
	private String arguments;
	
	private String popupBlockerMessage;
	
	
	public PopupResult(String url) {
		this.url = url;
	}
	
	@RemoteProperty
	public String getAction() {
		return "popup";
	}
	
	@RemoteProperty
	public String getPopupBlockerMessage() {
		return this.popupBlockerMessage;
	}

	public PopupResult setPopupBlockerMessage(String popupBlockerMessage) {
		this.popupBlockerMessage = popupBlockerMessage;
		return this;
	}

	@RemoteProperty
	public String getUrl() {
		return this.url;
	}

	@RemoteProperty
	public String getWindowName() {
		return this.windowName;
	}

	public PopupResult setWindowName(String windowName) {
		this.windowName = windowName;
		return this;
	}
	
	@RemoteProperty
	public String getArguments() {
		return arguments;
	}
	
	public PopupResult setArguments(String arguments) {
		this.arguments = arguments;
		return this;
	}
		
}
