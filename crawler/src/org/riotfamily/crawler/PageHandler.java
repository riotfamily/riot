/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
