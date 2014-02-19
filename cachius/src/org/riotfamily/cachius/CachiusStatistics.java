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

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class CachiusStatistics {

	private CacheService service;
	
	private Set<String> cacheRegionNames;

	private volatile long maxUpdateTime;
	
	private volatile String slowestUpdate;
	
	private AtomicLong hits = new AtomicLong();
	
	private AtomicLong misses = new AtomicLong();
	
	protected CachiusStatistics(CacheService service, Set<String> cacheRegionNames) {
		this.service = service;
		this.cacheRegionNames = cacheRegionNames;
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
		//service.getCache(null).resetOverflowStats();
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
	
	public int getCapacity(String region) {
		return service.getCache(region).getRegion().getCapacity();
	}
    
    public int getSize(String region) {
    	return service.getCache(region).getSize(); 
    }
    
    public void invalidateAllItems() {
    	for (String region : cacheRegionNames) {
    		service.getCache(region).invalidateAll();
    	}
    }    
    
    public Set<String> getCacheRegionNames() {
		return cacheRegionNames;
	}
    
    /*public long getAverageOverflowInterval() {
		return service.getCache(null).getAverageOverflowInterval();
	}
    
    public long getMaxInvalidationTime() {
		return service.getCache(null).getMaxInvalidationTime();
	}*/

}
