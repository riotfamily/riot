package org.riotfamily.forms.event;

import org.riotfamily.forms.Element;



/**
 *
 */
public class ChangeEvent {
	
	private Element source;
	
	private Object newValue;
	
	private Object oldValue;

	public Object getNewValue() {
		return newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}
	
	public Element getSource() {
		return source;
	}
	
	public ChangeEvent(Element source, Object newValue, Object oldValue) {
		this.source = source;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}
}
