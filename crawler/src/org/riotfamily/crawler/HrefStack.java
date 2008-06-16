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
