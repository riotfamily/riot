package org.riotfamily.search.crawler;


public interface CrawlerModel {

	public boolean hasNextUrl();
	
	public String getNextUrl();
	
	public void addUrl(String url);

}
