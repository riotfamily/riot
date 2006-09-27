package org.riotfamily.riot.workflow.status;


public class StatusMessage {

	private String text;
	
	private String link;
	
	public StatusMessage(String text, String link) {		
		this.text = text;
		this.link = link;
	}

	public String getLink() {
		return link;
	}

	public String getText() {
		return text;
	}

}
