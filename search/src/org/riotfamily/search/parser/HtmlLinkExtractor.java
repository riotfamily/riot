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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.search.parser;

import java.util.Collection;

import org.riotfamily.search.crawler.LinkExtractor;
import org.riotfamily.search.crawler.PageData;

public class HtmlLinkExtractor implements LinkExtractor {

	private PageParser pageParser;
	
	private PageHandler pageProcessor;
	
	public HtmlLinkExtractor(PageParser parser, PageHandler processor) {
		this.pageParser = parser;
		this.pageProcessor = processor;
	}

	public final Collection extractLinks(PageData pageData) {
		try {
			Page page = pageParser.parsePage(pageData);
			if (pageProcessor != null) {
				pageProcessor.handlePage(page);
			}
			return page.getLinks();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
