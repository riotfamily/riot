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
