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

import org.htmlparser.util.ParserException;
import org.riotfamily.components.event.ContentChangedEvent;
import org.springframework.context.ApplicationEvent;

public class ContentCrawler extends Crawler {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContentChangedEvent) {
			ContentChangedEvent ce = (ContentChangedEvent) event;
			log.info("Content changed: " + ce.getUrl());
			if (running) {
				hrefs.addAbsolute(ce.getUrl(), null);
			}
			PageData pageData = pageLoader.loadPage(new Href(null, ce.getUrl(), null));
	        if (pageData.isOk()) {
	        	try {
		        	pageData.parse();
	        	}
	        	catch (ParserException e) {
	        		log.error("Error parsing page", e);
	        	}
	        }
	        for (PageHandler handler : pageHandlers) {
	        	handler.handlePage(pageData);
	        }
		}
	}
	
}
