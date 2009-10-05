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

import java.util.List;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.springframework.util.ObjectUtils;

public class Statistics {

	private List<SimpleStatsItem> items = Generics.newArrayList();
	
	public void add(String name, Object value) {
		add(name, value, false);
	}
	
	public void add(String name, Object value, boolean critical) {
		items.add(new SimpleStatsItem(name, value, critical));
	}
	
	public void addOkIfNull(String name, Object value) {
		items.add(new SimpleStatsItem(name, value, value != null));
	}
	
	public void addOkIfEquals(String name, Object value, Object expected) {
		items.add(new SimpleStatsItem(name, value, !ObjectUtils.nullSafeEquals(value, expected)));
	}
	
	public void addOkBelow(String name, Number value, Number threshold) {
		items.add(new SimpleStatsItem(name, value, value.longValue() > threshold.longValue()));
	}
	
	public void addMillis(String name, long millis) {
		addMillis(name, millis, false);
	}
	
	public void addMillis(String name, long millis, boolean critical) {
		items.add(new SimpleStatsItem(name, FormatUtils.formatMillis(millis), critical));
	}
	
	public void addBytes(String name, long bytes) {
		addBytes(name, bytes, false);
	}
	
	public void addBytes(String name, long bytes, boolean critical) {
		items.add(new SimpleStatsItem(name, FormatUtils.formatByteSize(bytes), critical));
	}
		
	public List<SimpleStatsItem> getItems() {
		return items;
	}
}
