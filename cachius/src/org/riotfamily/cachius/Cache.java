package org.riotfamily.cachius;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.riotfamily.cachius.invalidation.ItemIndex;

public class Cache {
	
	private Region region;
	
	private ItemIndex index;
	
	private ConcurrentHashMap<String, CacheEntry> map =
			new ConcurrentHashMap<String, CacheEntry>();
	
	private AtomicInteger size = new AtomicInteger();
	
	private CleanUpThread cleanUpThread = new CleanUpThread();

	public Cache(Region region, ItemIndex index) {
		this.region = region;
		this.index = index;
		cleanUpThread.start();
	}

	public Region getRegion() {
		return region;
	}
	
	public int getSize() {
		return size.get();
	}

	/**
	 * Returns the CacheItem with the given key or creates a new one, if no
	 * entry with that key exists.
	 * 
	 * @param key The cache key
	 * @return The CacheItem for the given key
	 */
	public CacheEntry getEntry(String key) {
		CacheEntry entry = map.get(key);
		if (entry == null) {
			try {
				CacheEntry newEntry = new CacheEntry(key);
				CacheEntry oldEntry = map.putIfAbsent(key, newEntry);
				if (oldEntry == null) {
					size.incrementAndGet();
					checkCapacity();
					return newEntry;
				}
				else {
					return oldEntry;
				}
			}
			catch (Exception e) {
				//log.error("Error creating CacheItem", e);
				return null;
			}
		}
		else {
			return entry;
		}
	}
	
	/**
	 * Removes the given item from the cache.
	 */
	private void removeEntry(CacheEntry entry) {
		map.remove(entry.getKey());
		size.decrementAndGet();
		index.remove(entry.getItem());
		entry.delete();
	}
	
	/**
	 * Notifies the clean-up thread when the capacity is exceeded.
	 */
	private void checkCapacity() {
		if (size.get() >= region.getCapacity()) {
			synchronized (cleanUpThread) {
				cleanUpThread.notify();
			}
		}
	}
	
	/**
	 * Invalidates all cache entries
	 */
	protected void invalidateAll() {
		for (CacheEntry entry : map.values()) {
			entry.getItem().invalidate();
		}
	}	
	
	/**
	 * Removes items from the cache that haven't been used for a long time. The
	 * number of items removed is <code>capacity * evictionFactor</code>.
	 */
	private void cleanup() {
		TreeSet<CacheEntry> entries = new TreeSet<CacheEntry>(map.values());
		int i = region.getItemsToEvict();
		Iterator<CacheEntry> it = entries.iterator();
		while (it.hasNext() && i > 0) {
			removeEntry(it.next());
			i--;
		}
	}
	
	public void destroy() {
		cleanUpThread.shutdown();
	}
	
	/**
	 * Thread that performs a clean-up upon notification.
	 */
	private class CleanUpThread extends Thread {

		private boolean running = true;

		@Override
		public void run() {
			while (running) {
				synchronized (this) {
					try {
						wait();
					}
					catch (InterruptedException e) {
						break;
					}
				}
				if (running) {
					cleanup();
				}
			}
		}

		public synchronized void shutdown() {
			running = false;
			notify();
		}
	}

}
