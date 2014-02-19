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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Region {

	Logger log = LoggerFactory.getLogger(Region.class);
	
	private String name;
	
	private int capacity = 10000;
	
	private double evictionFactor = 0.2;
	
	private volatile long lastOverflow = System.currentTimeMillis();
	
	private volatile long averageOverflowInterval;

	public Region(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int getCapacity() {
		return capacity;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public void setEvictionFactor(double evictionFactor) {
		this.evictionFactor = evictionFactor;
	}
	
	public long getAverageOverflowInterval() {
		return averageOverflowInterval;
	}

	public int getItemsToEvict() {
		logOverflow();
		return (int) Math.ceil(capacity * evictionFactor);
	}
	
	public void logOverflow() {
		log.info("Cache capacity exceeded for region {}. Performing cleanup ...", name);
		long timeSinceLastOverflow = System.currentTimeMillis() - lastOverflow;
		if (averageOverflowInterval == 0) {
			averageOverflowInterval = timeSinceLastOverflow;
		}
		else {
			averageOverflowInterval = (averageOverflowInterval + timeSinceLastOverflow) / 2;
		}
	}
}
