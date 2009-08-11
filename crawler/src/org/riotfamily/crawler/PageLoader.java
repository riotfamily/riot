package org.riotfamily.crawler;

/**
 * Interface to load a page.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface PageLoader {

	public PageData loadPage(Href href);

}
