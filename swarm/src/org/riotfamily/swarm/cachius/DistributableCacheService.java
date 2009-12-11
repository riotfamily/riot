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
package org.riotfamily.swarm.cachius;

import java.util.Set;

import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.swarm.CacheChannelAdapter;
import org.riotfamily.swarm.RiotChannelListener;
import org.riotfamily.swarm.RiotEvent;

public class DistributableCacheService extends CacheService
	implements RiotChannelListener {

	private static final RiotLog log = RiotLog.get(DistributableCacheService.class);

	private transient CacheChannelAdapter channelAdapter;

	private DistributableCachiusStatistics stats;

	private ThreadLocal<DistributableDeferredCacheInvalidator> distributableDeferredCacheInvalidators;


	/**
	 * @param cache
	 * @param channelAdapter
	 */
	public DistributableCacheService(Cache cache,
			CacheChannelAdapter channelAdapter) {
		super(cache);

		this.channelAdapter = channelAdapter;
		this.stats = new DistributableCachiusStatistics(this);
		if (channelAdapter != null) {
			channelAdapter.addListener(this);
		}
		this.distributableDeferredCacheInvalidators =
			new ThreadLocal<DistributableDeferredCacheInvalidator>();

	}

	@Override
	public DistributableCachiusStatistics getStatistics() {
		return stats;
	}

	@Override
	public void beginLocallyDeferredInvalidation() {
		log.trace("Begin locally deferred distributed cache invalidation.");
		distributableDeferredCacheInvalidators.set(
			new DistributableDeferredCacheInvalidator(distributableDeferredCacheInvalidators.get()));
	}

	@Override
	protected CacheInvalidator getCacheInvalidator() {
		CacheInvalidator cacheInvalidator = distributableDeferredCacheInvalidators.get();
		if (cacheInvalidator == null) {
			cacheInvalidator = getDefaultCacheInvalidator();
		}
		return cacheInvalidator;
	}

	@Override
	public void commitLocallyDeferredInvalidation() {
		DistributableDeferredCacheInvalidator distributableDeferredCacheInvalidator =
			distributableDeferredCacheInvalidators.get();

		if (distributableDeferredCacheInvalidator != null) {
			log.trace("Commit and distribute locally deferred cache invalidation.");

			distributableDeferredCacheInvalidator.commit();
			distributableDeferredCacheInvalidators.set(
					(DistributableDeferredCacheInvalidator) distributableDeferredCacheInvalidator.getParent());
		} else {
			log.warn("Unbalanced commit of locally deferred cache " +
				"invalidation is ignored.");
		}
	}

	@Override
	protected void immediatelyInvalidateTaggedItems(String tag) {
		immediatelyInvalidateTaggedItems(tag, true);
	}

	private void immediatelyInvalidateTaggedItems(String tag, boolean publishInChannel) {
		if (publishInChannel && channelAdapter != null) {
			channelAdapter.invalidateCacheByTag(tag);
		}
		performCacheInvalidationByTag(tag);
	}

	protected void performCacheInvalidationByTag(String tag) {
		super.immediatelyInvalidateTaggedItems(tag);
	}

	protected void immediatelyInvalidateTaggedItems(String[] tags) {
		immediatelyInvalidateTaggedItems(tags, true);
	}

	private void immediatelyInvalidateTaggedItems(String[] tags, boolean publishInChannel) {
		if (tags == null || tags.length == 0) {
			log.trace("No tags to invalidate");
			return;
		}

		if (publishInChannel && channelAdapter != null) {
			channelAdapter.invalidateCacheByTags(tags);
		}

		for (String tag : tags) {
			performCacheInvalidationByTag(tag);
		}
	}

	protected void immediatelyInvalidateItemsByKeys(String[] keys) {
		immediatelyInvalidateItemsByKeys(keys, true);
	}

	private void immediatelyInvalidateItemsByKeys(String[] keys, boolean publishInChannel) {
		if (publishInChannel && channelAdapter != null) {
			channelAdapter.invalidateCacheByKeys(keys);
		}

		// no actual invalidation implemented
	}

	public void invalidateAllItems() {
		invalidateAllItems(true);
	}

	private void invalidateAllItems(boolean publishInChannel) {
		if (publishInChannel && channelAdapter != null) {
			channelAdapter.invalidateAll();
		}
		getStatistics().immediatelyInvalidateAllItems();
	}

	public boolean handleEvent(RiotEvent event) {
		boolean result = false;
		log.trace("Handling event: " + event);
		if (CacheChannelAdapter.INVALIDATE_BY_KEYS.equals(event.getMessageType())) {
			String[] keys = (String[])event.getParams();
			immediatelyInvalidateItemsByKeys(keys, false);
			result = true;
		} else if (CacheChannelAdapter.INVALIDATE_BY_TAGS.equals(event.getMessageType())) {
			String[] tags = (String[])event.getParams();
			immediatelyInvalidateTaggedItems(tags, false);
			result = true;
		} else if (CacheChannelAdapter.INVALIDATE_BY_TAG.equals(event.getMessageType())) {
			String tag = (String)event.getParam();
			immediatelyInvalidateTaggedItems(tag, false);
			result = true;
		} else if (CacheChannelAdapter.INVALIDATE_ALL.equals(event.getMessageType())) {
			invalidateAllItems(false);
		}
		return result;
	}

	protected class DistributableDeferredCacheInvalidator extends DeferredCacheInvalidator {

		public DistributableDeferredCacheInvalidator(DeferredCacheInvalidator parent) {
			super(parent);
		}

		@Override
		public void commit() {
			Set<String> tags = getTags();
			if (getParent() == null) {
				log.trace("Performing invalidation of items tagged with one " +
						"out of %d stored tags.", Integer.valueOf(tags.size()));
				// we have no more parent deferred invalidators, perform actual invalidation
				String[] tagsArray = tags.toArray(new String[tags.size()]);
				immediatelyInvalidateTaggedItems(tagsArray);
			} else {
				CacheInvalidator invalidator = getDownstreamInvalidator();
				for (String tag : tags) {
					invalidator.invalidateTaggedItems(tag);
				}
			}
		}
	}
}
