package org.riotfamily.cachius;

import java.util.concurrent.atomic.AtomicLong;

public class CachiusStatistics {

	private CacheService service;

	private volatile long maxUpdateTime;
	
	private volatile String slowestUpdate;
	
	private AtomicLong hits = new AtomicLong();
	
	private AtomicLong misses = new AtomicLong();
	
	protected CachiusStatistics(CacheService service) {
		this.service = service;
	}
	
	protected void addHit() {
		hits.incrementAndGet();
	}
	
	protected void addMiss() {
		misses.incrementAndGet();
	}
	
	protected void itemUpdated(CacheItem item, long time) {
		if (time > maxUpdateTime) {
			maxUpdateTime = time;
			slowestUpdate = item.toString();
		}
	}

	// Public methods --------------------------------------------------------
	
	public void reset() {
		maxUpdateTime = 0;
		slowestUpdate = null;
		hits.set(0);
		misses.set(0);
		service.getCache().resetOverflowStats();
	}
	
	public long getMaxUpdateTime() {
		return maxUpdateTime;
	}

	public String getSlowestUpdate() {
		return slowestUpdate;
	}

	public long getHits() {
		return hits.longValue();
	}

	public long getMisses() {
		return misses.longValue();
	}
	
	public int getCapacity() {
		return service.getCache().getCapacity();
	}
    
    public int getSize() {
		return service.getCache().getSize();
	}
    
    public int getNumberOfTags() {
    	return service.getCache().getNumberOfTags(); 
    }
    
    public long getAverageOverflowInterval() {
		return service.getCache().getAverageOverflowInterval();
	}
    
    public long getMaxInvalidationTime() {
		return service.getCache().getMaxInvalidationTime();
	}
	
    public void invalidateAllItems() {
    	service.getCache().invalidateAll();
    }
}
