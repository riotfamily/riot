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
