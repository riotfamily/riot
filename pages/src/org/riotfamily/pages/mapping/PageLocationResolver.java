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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;


/**
 * Interface that resolves the {@link PageLocation} for a request. The 
 * PageLocation can be passed to a PageDao in order to look up a Page.
 * <p>
 * Resolvers are virtually bidirectional as they can also be used to build 
 * URLs that point to a given Page.  
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public interface PageLocationResolver {

	/**
	 * Returns the {@link PageLocation} that should be used to look up the Page.
	 * Implementors may return <code>null</code> if it's obvious that a lookup 
	 * will not yield any page.
	 */
	public PageLocation getPageLocation(HttpServletRequest request);
	
	/**
	 * Returns an URL, which if requested will be mapped to the given Page.
	 * Implementors <em>should</em> return an URL relative to the context path
	 * but may also return an absolute URL (containing protocol and
	 * server name). 
	 */
	public String getUrl(Page page);

}
