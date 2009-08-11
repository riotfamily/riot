package org.riotfamily.forms.event;






/**
 *
 */
public class ClickEvent {

	private Button source;

	public ClickEvent(Button source) {
		this.source = source;
	}

	public Button getSource() {
		return source;
	}
}
