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
package org.riotfamily.search.index.html;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.util.NodeList;
import org.riotfamily.crawler.PageData;

/**
 * {@link FieldExtractor} that extracts the text from all nodes matched by a
 * CSS selector.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CssSelectorTextExtractor implements FieldExtractor {
	
	private NodeFilter nodeFilter;
	
	/**
	 * Sets a NodeFilter that is used to extract the field value.
	 */
	public void setNodeFilter(NodeFilter nodeFilter) {
		this.nodeFilter = nodeFilter;
	}
	
	/**
	 * Sets a CSS selector that is used to extract the field value.
	 */
	public void setCssSelector(String cssSelector) {
		nodeFilter = new CssSelectorNodeFilter(cssSelector);
	}
	
	/**
	 * Extracts the text from all nodes matched by the configured CSS selector.
	 * @see CssSelectorNodeFilter
	 * @see HtmlParserUtils#extractText(NodeList, NodeFilter)
	 */
	public String getFieldValue(PageData pageData) {
		return HtmlParserUtils.extractText(pageData.getNodes(), nodeFilter);			
	}
	
}
