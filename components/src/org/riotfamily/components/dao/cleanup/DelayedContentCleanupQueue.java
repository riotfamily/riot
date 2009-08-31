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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   alf
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.dao.cleanup;

import java.util.LinkedList;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.components.model.Content;

public class DelayedContentCleanupQueue {
	private static final RiotLog log = RiotLog.get(DelayedContentCleanupQueue.class);
	
	private LinkedList<Item> list;
	
	public DelayedContentCleanupQueue() {
		list = new LinkedList<Item>();
	}
	
	protected synchronized Item get(long minAge) {
		if (!list.isEmpty()) {
			if (list.getFirst().hasAge(minAge)) {
				return list.remove();
			}
		}
		
		return null;
	}

	public synchronized void put(Content content) {
		if (log.isDebugEnabled()) {
			log.debug("Content with id %d prepared for later deletion.",
				content.getId());
		}
		list.add(new Item(content));
	}
	
	protected class Item {
		private Long contentId;
		private long timestamp;
		
		private long getNow() {
			return System.currentTimeMillis() / 1000;
		}
		
		public Item(Content content) {
			this.contentId = content.getId();
			this.timestamp = getNow();
		}
		
		public long getAge() {
			return getNow() - this.timestamp;
		}
		
		public boolean hasAge(long age) {
			return getAge() >= age;
		}
		
		public Long getContentId() {
			return contentId;
		}
	}
}
