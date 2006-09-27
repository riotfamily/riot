package org.riotfamily.forms.ajax;

import java.util.List;

/**
 *
 */
class EventPropagation {
	
	public static final String ON_CLICK = "click";
	
	public static final String ON_CHANGE = "change";
	
	private String id;
	
	private String type;
	
	public EventPropagation(String id, String type) {
		this.id = id;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public static void addPropagations(JavaScriptEventAdapter adapter, List list) {
		int types = adapter.getEventTypes();
		if ((types & JavaScriptEvent.ON_CLICK) > 0) {
			list.add(new EventPropagation(adapter.getId(), ON_CLICK));
		}
		if ((types & JavaScriptEvent.ON_CHANGE) > 0) {
			list.add(new EventPropagation(adapter.getId(), ON_CHANGE));
		}
	}
	
}
