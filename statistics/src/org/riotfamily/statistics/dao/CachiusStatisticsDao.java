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
package org.riotfamily.statistics.dao;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CachiusStatistics;
import org.riotfamily.statistics.domain.Statistics;
import org.springframework.beans.factory.annotation.Required;

public class CachiusStatisticsDao extends AbstractSimpleStatsDao {

	private CachiusStatistics cachius;

	@Required
	public void setCacheService(CacheService service) {
		this.cachius = service.getStatistics();
	}
	
	public CachiusStatistics getCachiusStatistics() {
		return cachius;
	}
	
	@Override
	protected void populateStats(Statistics stats) throws Exception {
		stats.add("Capacity", cachius.getCapacity());
		stats.add("Cached items", cachius.getSize());
		//stats.addMillis("Average overflow interval", cachius.getAverageOverflowInterval());
		//stats.add("Max invalidation time [ms]", cachius.getMaxInvalidationTime());
		
		stats.add("Hits", cachius.getHits());
		stats.add("Misses", cachius.getMisses());
		
		stats.add("Max update time [ms]", cachius.getMaxUpdateTime());
		stats.add("Slowest update", cachius.getSlowestUpdate());
	}
}
