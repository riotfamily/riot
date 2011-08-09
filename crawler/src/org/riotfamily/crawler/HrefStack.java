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

	public void add(String baseUri, String uri, String referrerUrl) {
		Href href = new Href(baseUri, uri, referrerUrl);
		if (!knownHrefs.contains(href)) {
			knownHrefs.add(href);
			stack.push(href);
		}
	}
	
	public void addAbsolute(String uri, String referrerUrl) {
		Href href = new Href(null, uri, referrerUrl);
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
