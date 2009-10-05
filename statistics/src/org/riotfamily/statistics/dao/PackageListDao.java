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

import java.util.Collection;

import org.riotfamily.common.util.PackageLister;
import org.riotfamily.statistics.domain.Statistics;

public class PackageListDao extends AbstractSimpleStatsDao {

	private String[] patterns;
	
	public void setPatterns(String[] patterns) {
		this.patterns = patterns;
	}
	
	@Override
	protected void populateStats(Statistics stats) throws Exception {
		Collection<Package> packages = PackageLister.listPackages(patterns);
		for (Package pack : packages) {
			stats.add(pack.getImplementationTitle(), pack.getImplementationVersion());
		}
	}
	
}
