package org.riotfamily.cachius.invalidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.riotfamily.cachius.CacheItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemIndex {

	private Logger log = LoggerFactory.getLogger(ItemIndex.class);
	
	private ConcurrentHashMap<String, List<CacheItem>> taggedItems =
			new ConcurrentHashMap<String, List<CacheItem>>();
	
	public void add(CacheItem item) {
		Set<String> tags = item.getTags();
		if (tags != null) {
			for (String tag : tags) {
				log.debug("Tagging item with {}", tag);
				List<CacheItem> items = taggedItems.get(tag);
				if (items == null) {
					List<CacheItem> newItems = new ArrayList<CacheItem>();
					List<CacheItem> oldItems = taggedItems.putIfAbsent(tag, newItems);
					items = oldItems != null ? oldItems : newItems;
				}
				synchronized (items) {
					items.add(item);
				}
			}
		}
	}
	
	public void remove(CacheItem item) {
		Set<String> tags = item.getTags();
		if (tags != null) {
			for (String tag : tags) {
				List<CacheItem> items = taggedItems.get(tag);
				if (items != null) {
					synchronized (items) {
						items.remove(item);
						if (items.isEmpty()) {
							taggedItems.remove(tag);
						}
					}
				}
			}
		}
	}
	
	public void invalidate(String tag) {
		if (tag != null) {
			log.debug("Invalidating items tagged with {}", tag);
			List<CacheItem> items = taggedItems.get(tag);
			if (items != null) {
				synchronized (items) {
					for (CacheItem item : items) {
						item.invalidate();
					}
				}
			}
		}
	}

}
