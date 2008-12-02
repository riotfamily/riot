package org.riotfamily.linkcheck;

import java.util.Collection;

public interface LinkDao {

	public Link loadLink(String source, String destination);
	
	public void deleteAll();
	
	public void saveAll(Collection<Link> links);

	public void deleteBrokenLinksTo(String destUrl);
	
	public void deleteBrokenLinksFrom(String sourceUrl);
	
	public int countBrokenLinks();
	
	public Collection<Link> findAllBrokenLinks();
	
	public Collection<String> findBrokenLinksOnPage(String url);

}
