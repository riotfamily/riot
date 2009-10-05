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
package org.riotfamily.statistics.dao;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;

import org.riotfamily.statistics.domain.Statistics;

public class RuntimeStatisticsDao extends AbstractSimpleStatsDao {

	private String systemEncoding;
	
	public RuntimeStatisticsDao() {
		systemEncoding = new OutputStreamWriter(new ByteArrayOutputStream()).getEncoding();
	}
	
	@Override
	protected void populateStats(Statistics stats) throws Exception {
		stats.add("Local host name", InetAddress.getLocalHost().getHostName());
		
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
