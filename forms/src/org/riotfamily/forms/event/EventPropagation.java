/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
