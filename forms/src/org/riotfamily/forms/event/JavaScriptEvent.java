package org.riotfamily.forms.event;

import javax.servlet.http.HttpServletRequest;

/**
 * Serverside representation of a clientside JavaScript event.
 */
public class JavaScriptEvent {

	public static final int NONE = 0;
	
	public static final int ON_CLICK = 1;

	public static final int ON_CHANGE = 2;

	private int type;

	private String value;
	
	private String[] values;

	public JavaScriptEvent(HttpServletRequest request) {
		String submittedType = request.getParameter("event.type");
		if (submittedType.equals("click")) {
			this.type = ON_CLICK;
		}
		if (submittedType.equals("change")) {
			this.type = ON_CHANGE;
		}
		this.values = request.getParameterValues("source.value");
		this.value = request.getParameter("source.value");
	}

	public int getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String[] getValues() {
		return this.values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
	
}