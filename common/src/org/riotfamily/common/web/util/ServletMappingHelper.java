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
package org.riotfamily.common.web.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

/**
 * @deprecated Please use an UrlPathHelper or the {@link ServletUtils}.
 */
public class ServletMappingHelper extends UrlPathHelper {
	
	boolean useOriginalRequest = false;
		
	public ServletMappingHelper() {
	}
	
	public ServletMappingHelper(boolean useOriginalRequest) {
		this.useOriginalRequest = useOriginalRequest;
	}

	public void setUseOriginalRequest(boolean useOriginalRequest) {
		this.useOriginalRequest = useOriginalRequest;
	}

	public String getLookupPathForRequest(HttpServletRequest request) {
		if (useOriginalRequest) {
			return ServletUtils.getLookupPathForOriginatingRequest(request);
		}
		return super.getLookupPathForRequest(request);
	}
			
	public String getServletPath(HttpServletRequest request) {
		if (useOriginalRequest) {
			return ServletUtils.getOriginatingServletPath(request);
		}
		return super.getServletPath(request);
	}

	public String getContextPath(HttpServletRequest request) {
		if (useOriginalRequest) {
			return getOriginatingContextPath(request);
		}
		return super.getContextPath(request);
	}

	public String getRequestUri(HttpServletRequest request) {
		if (useOriginalRequest) {
			return getOriginatingRequestUri(request);
		}
		return super.getRequestUri(request);
	}
	
	public String getQueryString(HttpServletRequest request) {
		if (useOriginalRequest) {
			return getOriginatingQueryString(request);
		}
		return request.getQueryString();
	}

	public String getServletPrefix(HttpServletRequest request) {
		return ServletUtils.getServletPrefix(request);
	}
	
	public String getServletSuffix(HttpServletRequest request) {
		return ServletUtils.getServletSuffix(request);
	}
	
	public String getRootPath(HttpServletRequest request) {
		return ServletUtils.getRootPath(request);
	}

}
