package org.riotfamily.crawler;

import java.util.HashSet;
import java.util.Stack;



/**
 * A bookkeeping stack of hypertext references that memorizes visited URLs to 
 * guarantee that each link is only processed once.
 * 
 * @author Felix Gnass [fgnass at neteye dot de] 
 */
public class HrefStack {
	
	private HashSet<Href> knownHrefs = new HashSet<Href>();

	private Stack<Href> stack = new Stack<Href>();

	public boolean hasNext() {
		return !stack.isEmpty();
	}

	public Href next() {
		return stack.pop();
	}

	public void add(String baseUri, String uri) {
		Href href = new Href(baseUri, uri);
		if (!knownHrefs.contains(href)) {
			knownHrefs.add(href);
			stack.push(href);
		}
	}
	
	public void addAbsolute(String uri) {
		Href href = new Href(null, uri);
		if (!stack.contains(href)) {
			stack.push(href);
			knownHrefs.add(href);
		}
	}
	
	public void clear() {
		knownHrefs.clear();
		stack.clear();
	}
	
}
