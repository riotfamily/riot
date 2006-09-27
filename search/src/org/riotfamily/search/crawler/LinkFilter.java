package org.riotfamily.search.crawler;

public interface LinkFilter {

	public boolean accept(String baseUrl, String link);

}
