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
package org.riotfamily.statistics.domain;

public class CacheRegionStatsItem extends StatsItem {
	
	private Long elementsInMemory;
	
	private Long elementsOnDisk;
	
	private Long hitCount;
	
	private Long missCount;
	
	private Long putCount;
	
	private Long kbInMemory;

	public CacheRegionStatsItem(String name) {
		super(name);
	}
	
	public Long getElementsInMemory() {
		return elementsInMemory;
	}

	public void setElementsInMemory(Long elementsInMemory) {
		this.elementsInMemory = elementsInMemory;
	}

	public Long getElementsOnDisk() {
		return elementsOnDisk;
	}

	public void setElementsOnDisk(Long elementsOnDisk) {
		this.elementsOnDisk = elementsOnDisk;
	}

	public Long getHitCount() {
		return hitCount;
	}

	public void setHitCount(Long hitCount) {
		this.hitCount = hitCount;
	}

	public Long getMissCount() {
		return missCount;
	}

	public void setMissCount(Long missCount) {
		this.missCount = missCount;
	}

	public Long getPutCount() {
		return putCount;
	}

	public void setPutCount(Long putCount) {
		this.putCount = putCount;
	}

	public Long getKbInMemory() {
		return kbInMemory;
	}

	public void setKbInMemory(Long kbInMemory) {
		this.kbInMemory = kbInMemory;
	}

}
