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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * Default LinkExtractor implementation that extracts the href attributes
 * of all A, LINK and AREA tags, unless a rel="nofollow" or rel="stylesheet" 
 * attribute is present.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DefaultLinkExtractor implements LinkExtractor {

	private NodeFilter nodeFilter = new LinkNodeFilter();
	
	public List<String> extractLinks(PageData pageData) {
		NodeList nodes = pageData.getNodes();
		if (nodes == null) {
			return Collections.emptyList();
		}
		NodeList linkNodes = nodes.extractAllNodesThatMatch(nodeFilter, true);
		ArrayList<String> links = new ArrayList<String>(linkNodes.size());
		SimpleNodeIterator it = linkNodes.elements();
		while (it.hasMoreNodes()) {
			Tag tag = (Tag) it.nextNode();
			String href = tag.getAttribute("href");
			href = href.trim().replaceAll("&amp;", "&");
			links.add(href);
		}
		return links;
	}
	
	private static class LinkNodeFilter implements NodeFilter {

		public boolean accept(Node node) {
			if (node instanceof Tag) {
				Tag tag = (Tag) node;
				String name = tag.getTagName().toUpperCase();
				if (name.equals("A") || name.equals("LINK") || name.equals("AREA")) {
					if (tag.getAttribute("href") != null) {
						String rel = tag.getAttribute("rel");
						if (rel == null) {
							return true;
						}
						rel = rel.toLowerCase();
						return rel.indexOf("nofollow") == -1 
								&& !rel.equals("stylesheet");
					}
				}
			}
			return false;
		}

	}
}
