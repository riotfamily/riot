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

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import org.riotfamily.statistics.domain.Statistics;

public class RuntimeStatisticsDao extends AbstractSimpleStatsDao {

	private String systemEncoding;
	
	public RuntimeStatisticsDao() {
		systemEncoding = new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding();
	}
	
	@Override
	protected void populateStats(Statistics stats) throws Exception {
		Runtime rt = Runtime.getRuntime();
		long used = rt.totalMemory() - rt.freeMemory();
		boolean critical = used > rt.maxMemory() * 80 / 100;
		
		stats.addBytes("Max memory", rt.maxMemory());
		stats.addBytes("Used memory", used, critical);
		
		stats.addBytes("Free memory", rt.freeMemory());
		stats.addBytes("Total memory", rt.totalMemory());
		
		stats.add("Number of processors", rt.availableProcessors());
		stats.add("Active threads", Thread.activeCount());
		stats.addOkIfEquals("System encoding", systemEncoding, "UTF8");
	}
}
