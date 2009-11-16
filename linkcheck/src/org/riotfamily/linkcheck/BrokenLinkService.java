package org.riotfamily.linkcheck;

import java.util.Collection;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;

@RemoteProxy(name="BrokenLinkService")
public class BrokenLinkService {
	
	@RemoteMethod	
	public Collection<String> getBrokenLinks(String url) {		
		return BrokenLink.findBrokenLinksOnPage(url);
	}
	
}
