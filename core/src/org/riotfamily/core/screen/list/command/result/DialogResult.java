package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteMethod;
import org.riotfamily.core.screen.list.command.CommandResult;

@DataTransferObject
public class DialogResult implements CommandResult {

	private String url;
	
	private String content;
	
	private String title;
	
	private boolean closeButton;
	
	public DialogResult() {
	}
	
	public DialogResult setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public DialogResult setContent(String content) {
		this.content = content;
		return this;
	}
	
	public DialogResult setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public DialogResult setCloseButton(boolean closeButton) {
		this.closeButton = closeButton;
		return this;
	}
	
	@RemoteMethod
	public String getAction() {
		return "dialog";
	}

	@RemoteMethod
	public String getUrl() {
		return this.url;
	}
	
	@RemoteMethod
	public String getContent() {
		return content;
	}
	
	@RemoteMethod
	public String getTitle() {
		return title;
	}
	
	@RemoteMethod
	public boolean isCloseButton() {
		return closeButton;
	}

}
