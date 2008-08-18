package org.riotfamily.statistics.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.riotfamily.common.util.PackageLister;

public class PackageListDao extends AbstractPropertiesDao {

	private String[] patterns;
	
	public void setPatterns(String[] patterns) {
		this.patterns = patterns;
	}
	
	protected Map getProperties() {
		Map result = new HashMap();
		Collection packages = PackageLister.listPackages(patterns);
		for (Iterator iterator = packages.iterator(); iterator.hasNext();) {
			Package pack = (Package) iterator.next();
			result.put(pack.getImplementationTitle(), pack.getImplementationVersion());
		}
		return result;
	}

}
