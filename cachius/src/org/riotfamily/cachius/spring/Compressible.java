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
package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;

/**
 * Interface that can be implemented by controllers that deliver compressible
 * content.
 * 
 * @see <a href="http://developer.yahoo.com/performance/rules.html#gzip">Best Practices for Speeding Up Your Web Site</a>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface Compressible {

	/**
	 * Returns whether the response should be gzipped. Implementors will 
	 * usually return <code>true</code>, unless they serve multiple content
	 * types which and not all of them are eligible for compression. They 
	 * <strong>don't have to</strong> check whether the client supports gzip 
	 * compression as all compatibility checks are done by Cachius 
	 * {@link CacheService#responseCanBeZipped(HttpServletRequest) internally}. 
	 */
	public boolean gzipResponse(HttpServletRequest request);

}
