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
package org.riotfamily.revolt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.Assert;

/**
 * @author Alf Werder <alf dot werder at artundweise dot de>
 * @since 7.0
 */
public class EvolutionHistoryList implements Iterable<EvolutionHistory> {
	
	private List<EvolutionHistory> list;
	
	public EvolutionHistoryList(Collection<EvolutionHistory> histories) {
		list = new ArrayList<EvolutionHistory>();
		for (EvolutionHistory history : histories) {
			add(history);
		}
	}
	
	private void add(EvolutionHistory history) {
		for (int i = 0; i < list.size(); i++) {
			if (fitsOnPosition(i, history)) {
				list.add(i, history);
				return;
			}
		}
		
		if (fitsOnPosition(list.size(), history)) {
			list.add(history);
			return;
		}
		
		throw new IllegalStateException("Cyclic module dependency detected!");
	}
	
	
	private boolean fitsOnPosition(int i, EvolutionHistory history) {
		Assert.isTrue(i>=0);
		Assert.isTrue(i<=list.size());
		
		for (int j=0; j<i; j++) {
			EvolutionHistory before = (EvolutionHistory) list.get(j);
			if (before.dependsOn(history)) {
				return false;
			}
		}
		
		for (int j=i; j<list.size(); j++) {
			EvolutionHistory after = (EvolutionHistory) list.get(j);
			if (history.dependsOn(after)) {
				return false;
			}
		}
		
		return true;
	}

	public Iterator<EvolutionHistory> iterator() {
		return list.iterator();
	}
}
