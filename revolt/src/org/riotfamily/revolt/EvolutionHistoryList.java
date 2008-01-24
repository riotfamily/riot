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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
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
public class EvolutionHistoryList {
	private List list;
	
	public EvolutionHistoryList(Collection histories) {
		list = new ArrayList();
		
		Iterator i = histories.iterator();
		while (i.hasNext()) {
			EvolutionHistory history = (EvolutionHistory) i.next();
			add(history);
		}
	}
	
	private void add(EvolutionHistory history) {
		for (int i=0; i<list.size(); i++) {
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

	public Iterator iterator() {
		return list.iterator();
	}
}
