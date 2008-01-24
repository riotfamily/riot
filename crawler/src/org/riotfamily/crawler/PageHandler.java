/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
