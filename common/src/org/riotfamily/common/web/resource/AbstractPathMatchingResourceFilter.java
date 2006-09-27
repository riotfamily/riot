package org.riotfamily.common.web.resource;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public abstract class AbstractPathMatchingResourceFilter 
		implements ResourceFilter {

	private String[] matches;
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	public void setMatch(String match) {
		this.matches = new String[] { match };
	}
	
	public void setMatches(String[] matches) {
		this.matches = matches;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
	
	public boolean matches(String path) {
		for (int i = 0; i < matches.length; i++) {
			if (pathMatcher.match(matches[i], path)) {
				return true;
			}
		}
		return false;
	}

}
