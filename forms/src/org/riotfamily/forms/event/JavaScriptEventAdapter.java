package org.riotfamily.forms.event;





/**
 * Interface to be implemented by elements that want to be notified of 
 * client-side JavaScript events.
 */
public interface JavaScriptEventAdapter {

	public String getId();
	
	public String getEventTriggerId();
	
	/**
	 * Returns a bitmask describing which client-side events should be 
	 * propagated to the server.
	 */
	public int getEventTypes();
	
	public void handleJavaScriptEvent(JavaScriptEvent event);
}
