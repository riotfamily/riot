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
package org.riotfamily.components.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ContextMap implements Serializable {

	private transient Map map;
	
	public void removeExpiredContexts() {
		if (map != null) {
			Iterator it = map.values().iterator();
			while (it.hasNext()) {
				PageRequestContexts contexts = (PageRequestContexts) it.next();
				if (contexts.isExpired()) {
					it.remove();
				}
			}
		}
	}
	
	public void touch(String pageUri) {
		if (map != null) {
			PageRequestContexts contexts = (PageRequestContexts) map.get(pageUri);
			if (contexts != null) {
				contexts.touch();
			}
		}
	}
	
	public void put(String pageUri, Object contextKey, 
			PageRequestContext context, long timeToLive) {
		
		if (map == null) {
			 map = new HashMap();
		}
		PageRequestContexts contexts = (PageRequestContexts) map.get(pageUri);
		if (contexts == null) {
			contexts = new PageRequestContexts(timeToLive);
			map.put(pageUri, contexts);
		}
		contexts.put(contextKey, context);
	}
	
	public PageRequestContext get(String pageUri, Object contextKey) {
		if (map == null) {
			return null;
		}
		PageRequestContexts contexts = (PageRequestContexts) map.get(pageUri);
		if (contexts == null) {
			return null;
		}
		return contexts.get(contextKey);
	}

}
