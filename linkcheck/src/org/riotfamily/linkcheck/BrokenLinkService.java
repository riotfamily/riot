package org.riotfamily.linkcheck;

import java.util.Collection;


public class BrokenLinkService {
	
	private LinkDao dao;

	public BrokenLinkService(LinkDao dao) {
		this.dao = dao;
	}
	
	public Collection<String> getBrokenLinks(String url) {		
		return dao.findBrokenLinksOnPage(url);
	}
	
}
