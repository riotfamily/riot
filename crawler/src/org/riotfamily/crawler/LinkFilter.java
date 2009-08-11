package org.riotfamily.crawler;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public interface LinkFilter {

	public boolean accept(String base, String href);

}
