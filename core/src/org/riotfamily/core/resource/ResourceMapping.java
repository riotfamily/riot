package org.riotfamily.core.resource;

import java.io.IOException;

import org.riotfamily.common.util.RiotLog;
import org.springframework.core.io.Resource;

public class ResourceMapping {

	private RiotLog log = RiotLog.get(ResourceMapping.class);
	
	private String path;
	
	private Resource location;

	private boolean skip;
	
	public Resource getLocation() {
		return this.location;
	}

	public void setLocation(Resource location) {
		this.location = location;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public Resource getResource(String path) throws IOException {
		if (!skip && path.startsWith(this.path)) {
			String relativePath = path.substring(this.path.length());
			Resource res = location.createRelative(relativePath);
			log.debug("Looking for resource: " + res);
			if (res.exists()) {
				return res;
			}
		}
		return null;
	}

}
