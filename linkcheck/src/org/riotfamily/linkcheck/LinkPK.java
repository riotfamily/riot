package org.riotfamily.linkcheck;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class LinkPK implements Serializable {
	
	private String destination;
	
	private String source;
	
	public String getDestination() {
		return destination;
	}

	public String getSource() {
		return source;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setSource(String source) {
		this.source = source;
	}	

}
