package org.riotfamily.forms.ajax;

/**
 * Interface to be implemented by elements that want to be notified of 
 * clientside JavaScript events.
 */
public interface JavaScriptEventAdapter {

	public String getId();
	
	/**
	 * Returns a bitmask describing which clientside events should be 
	 * propagated to the server.
	 */
	public int getEventTypes();
	
	public void handleJavaScriptEvent(JavaScriptEvent event);
}
