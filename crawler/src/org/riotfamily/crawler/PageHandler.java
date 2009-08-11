package org.riotfamily.crawler;

/**
 * Interface that can be implemented by modules that want to process a page
 * when it is crawled.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface PageHandler {

	/**
	 * Invoked when the crawler starts to crawl.  
	 */
	public void crawlerStarted();
	
	/**
	 * Invoked for each page that is encountered during a crawler run.
	 */
	public void handlePage(PageData pageData);
	
	/**
	 * Invoked when the crawler has finished crawling.
	 */
	public void crawlerFinished();
	
	/**
	 * Invoked when the crawler fetches a single page, due to an 
	 * ApplicationEvent indicating that the page was modified.
	 */
	public void handlePageIncremental(PageData pageData);

}
