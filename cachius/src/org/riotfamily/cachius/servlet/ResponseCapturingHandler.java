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
package org.riotfamily.cachius.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheItem;

/**
 * Abstract CacheHandler that creates a response wrapper and captures
 * everything that is written to the response's OutputStream or Writer.
 */
public abstract class ResponseCapturingHandler extends SessionIdCacheHandler {

	private HttpServletResponse response;
	
	public ResponseCapturingHandler(HttpServletRequest request,
			HttpServletResponse response, 
			CacheKeyAugmentor cacheKeyAugmentor) {
		
		super(request, cacheKeyAugmentor);
		this.response = response;
	}

	protected boolean updateCacheItemInternal(CacheItem cacheItem) throws Exception {
		CachiusResponseWrapper wrapper = new CachiusResponseWrapper(
				response, cacheItem, getSessionIdEncoder());
		
		handleInternal(wrapper);
		
		wrapper.stopCapturing();
		wrapper.updateHeaders();
		return wrapper.isOk();
	}
	
	public void handleUncached() throws Exception {
		handleInternal(response);
	}
	
	protected abstract void handleInternal(HttpServletResponse response) 
			throws Exception;
	
	protected final void writeCacheItemInternal(CacheItem cacheItem) throws IOException {
		HttpServletResponse wrapper = response;
		if (getSessionIdEncoder().urlsNeedEncoding()) {
			wrapper = new SessionIdInsertingResponseWrapper(response, getSessionIdEncoder());
		}
		cacheItem.writeTo(wrapper);
	}
}
