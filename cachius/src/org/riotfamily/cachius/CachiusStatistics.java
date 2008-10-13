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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius;

import java.util.concurrent.atomic.AtomicLong;

public class CachiusStatistics {

	private CacheService service;

	private volatile long maxReadLockAcquisitionTime;
	
	private volatile String slowestReadLock;
	
	private volatile long maxWriteLockAcquisitionTime;
	
	private volatile String slowestWriteLock;
	
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
	
	protected void readLockAcquired(CacheItem item, long time) {
		if (time > maxReadLockAcquisitionTime) {
			maxReadLockAcquisitionTime = time;
			slowestReadLock = item.getKey();
		}
	}
	
	protected void writeLockAcquired(CacheItem item, long time) {
		if (time > maxWriteLockAcquisitionTime) {
			maxWriteLockAcquisitionTime = time;
			slowestWriteLock = item.getKey();
		}
	}
	
	protected void itemUpdated(CacheItem item, long time) {
		if (time > maxUpdateTime) {
			maxUpdateTime = time;
			slowestUpdate = item.getKey();
		}
	}

	// Public methods --------------------------------------------------------
	
	public void reset() {
		maxReadLockAcquisitionTime = 0;
		slowestReadLock = null;
		maxWriteLockAcquisitionTime = 0;
		slowestWriteLock = null;
		maxUpdateTime = 0;
		slowestUpdate = null;
		hits.set(0);
		misses.set(0);
		service.getCache().resetOverflowStats();
	}
	
	public long getMaxReadLockAcquisitionTime() {
		return maxReadLockAcquisitionTime;
	}
	
	public String getSlowestReadLock() {
		return slowestReadLock;
	}
	
	public long getMaxWriteLockAcquisitionTime() {
		return maxWriteLockAcquisitionTime;
	}
	
	public String getSlowestWriteLock() {
		return slowestWriteLock;
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
