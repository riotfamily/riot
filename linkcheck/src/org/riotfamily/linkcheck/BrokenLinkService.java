package org.riotfamily.linkcheck;

import java.util.Collection;


public class BrokenLinkService {
	
	public Collection<String> getBrokenLinks(String url) {		
		return Link.findBrokenLinksOnPage(url);
	}
	
}
