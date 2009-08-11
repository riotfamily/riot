package org.riotfamily.forms.event;

import java.util.List;





/**
 *
 */
public class EventPropagation {
	
	public static final String ON_CLICK = "click";
	
	public static final String ON_CHANGE = "change";
	
	private String triggerId;
	
	private String type;
	
	private String sourceId;
	
	public EventPropagation(String triggerId, String type, String sourceId) {
		this.triggerId = triggerId;
		this.type = type;
		this.sourceId = sourceId;
	}
	
	public String getTriggerId() {
		return triggerId;
	}
	
	public String getSourceId() {
		return sourceId;
	}
	
	public String getType() {
		return type;
	}
	
	public static void addPropagations(JavaScriptEventAdapter adapter, List<EventPropagation> list) {
		int types = adapter.getEventTypes();
		if ((types & JavaScriptEvent.ON_CLICK) > 0) {
			list.add(new EventPropagation(adapter.getEventTriggerId(), ON_CLICK, adapter.getId()));
		}
		if ((types & JavaScriptEvent.ON_CHANGE) > 0) {
			list.add(new EventPropagation(adapter.getEventTriggerId(), ON_CHANGE, adapter.getId()));
		}
	}
	
}
