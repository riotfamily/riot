package org.riotfamily.crawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;
import org.riotfamily.common.web.util.ServletUtils;

/**
 * Default LinkExtractor implementation that extracts the href attributes
 * of all A, LINK and AREA tags, unless a rel="nofollow" or rel="stylesheet" 
 * attribute is present.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DefaultLinkExtractor implements LinkExtractor {

	private NodeFilter nodeFilter = new LinkNodeFilter();
		
	public List extractLinks(PageData pageData) {
		NodeList nodes = pageData.getNodes();
		if (nodes == null) {
			return Collections.EMPTY_LIST;
		}
		NodeList linkNodes = nodes.extractAllNodesThatMatch(nodeFilter, true);
		ArrayList links = new ArrayList(linkNodes.size());
		SimpleNodeIterator it = linkNodes.elements();
		while (it.hasMoreNodes()) {
			Tag tag = (Tag) it.nextNode();
			String href = tag.getAttribute("href");
			href = href.trim().replaceAll("&amp;", "&");
			if (accept(pageData.getUrl(), href)) {
				links.add(href);
			}
		}
		return links;
	}
	
	protected boolean accept(String base, String uri) {
		if (!ServletUtils.isAbsoluteUrl(uri)) {
			// Always follow relative links
			return true;
		}
		String host = ServletUtils.getHost(uri);
		if (host == null) {
			// Scheme but no host - must be something link javascript: or mailto:
			return false;
		}
		if (base != null) {
			String baseHost = ServletUtils.getHost(base);
			if (host.equals(baseHost)) {
				// Absolute link to same host so we follow it ...
				return true;
			}
		}
		return false;
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
