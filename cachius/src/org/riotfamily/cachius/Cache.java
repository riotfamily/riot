package org.riotfamily.cachius;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Cache {
	
	private ConcurrentHashMap<String, CacheEntry> map =
			new ConcurrentHashMap<String, CacheEntry>();
	
	private transient AtomicInteger size = new AtomicInteger();
	
	private transient int capacity;
	
	private double evictionFactor = 0.2;
	
	private transient volatile long lastOverflow = System.currentTimeMillis();

	private transient volatile long averageOverflowInterval;

	private transient CleanUpThread cleanUpThread = new CleanUpThread();

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
		//removeFromIndex(entry.getItem());
		entry.delete();
	}
	
	/**
	 * Notifies the clean-up thread when the capacity is exceeded.
	 */
	private void checkCapacity() {
		if (size.get() >= capacity) {
			synchronized (cleanUpThread) {
				cleanUpThread.notify();
			}
		}
	}
	
	/**
	 * Removes items from the cache that haven't been used for a long time. The
	 * number of items removed is <code>capacity * evictionFactor</code>.
	 */
	private void cleanup() {
		//log.info("Cache capacity exceeded. Performing cleanup ...");
		long timeSinceLastOverflow = System.currentTimeMillis() - lastOverflow;
		if (averageOverflowInterval == 0) {
			averageOverflowInterval = timeSinceLastOverflow;
		}
		else {
			averageOverflowInterval = (averageOverflowInterval + timeSinceLastOverflow) / 2;
		}
		TreeSet<CacheEntry> entries = new TreeSet<CacheEntry>(map.values());
		int i = (int) Math.ceil(capacity * evictionFactor);
		Iterator<CacheEntry> it = entries.iterator();
		while (it.hasNext() && i > 0) {
			removeEntry(it.next());
			i--;
		}
	}
	
	public void shutdown() {
		cleanUpThread.shutdown();
	}
	
	/**
	 * Thread that performs a clean-up upon notification.
	 */
	private class CleanUpThread extends Thread {

		private boolean running = true;

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
