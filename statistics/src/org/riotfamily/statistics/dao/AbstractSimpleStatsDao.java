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

import java.util.List;

import org.riotfamily.statistics.domain.Statistics;
import org.riotfamily.statistics.domain.StatsItem;

public abstract class AbstractSimpleStatsDao extends AbstractStatsItemDao {

	@Override
	protected List<? extends StatsItem> getStats() throws Exception {
		Statistics stats = new Statistics();
		populateStats(stats);
		return stats.getItems();
	}
	
	protected abstract void populateStats(Statistics stats) throws Exception;
}
