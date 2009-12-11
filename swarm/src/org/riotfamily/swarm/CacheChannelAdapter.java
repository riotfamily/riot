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
 *   mgaudig
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.swarm;


public class CacheChannelAdapter extends AbstractRiotChannelAdapter {

	public static final String INVALIDATE_BY_KEYS = "cache-invalidate-by-keys";

	public static final String INVALIDATE_BY_TAG = "cache-invalidate-by-tag";

	public static final String INVALIDATE_BY_TAGS = "cache-invalidate-by-tags";

	public static final String INVALIDATE_ALL = "cache-invalidate-all";


	public CacheChannelAdapter(RiotChannel channel) {
		super(channel);
	}

	public void invalidateCacheByKeys(String[] itemKeys) {
		if (!channelExists()) return;
		RiotEvent event = new RiotEvent(INVALIDATE_BY_KEYS);
		event.setParams(itemKeys);
		sendEvent(event);
	}

	public void invalidateCacheByTag(String tag) {
		if (!channelExists()) return;
		RiotEvent event = new RiotEvent(INVALIDATE_BY_TAG);
		event.setParam(tag);
		sendEvent(event);
	}

	public void invalidateCacheByTags(String[] tags) {
		if (!channelExists()) return;
		RiotEvent event = new RiotEvent(INVALIDATE_BY_TAGS);
		event.setParams(tags);
		sendEvent(event);
	}

	public void invalidateAll() {
		if (!channelExists()) return ;
		RiotEvent event = new RiotEvent(INVALIDATE_ALL);
		sendEvent(event);
	}
}
