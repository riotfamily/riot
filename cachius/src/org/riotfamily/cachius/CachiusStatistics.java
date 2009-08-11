/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
