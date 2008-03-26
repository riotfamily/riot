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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.render;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.support.SessionUtils;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;

/**
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4
 */
public class DefaultComponentListCacheKeyProvider
	implements ComponentListCacheKeyProvider {

	public String getComponentCacheKey(HttpServletRequest request,
		ComponentVersion version) {
		
		StringBuffer key = new StringBuffer();
		key.append(version.getClass().getName());
		key.append('#');
		key.append(version.getId());
		SessionUtils.addStateToCacheKey(request, key);
		return key.toString();
	}

	public String getComponentListCacheKey(HttpServletRequest request,
		VersionContainer parent, String path, String key) {
		
		StringBuffer sb = new StringBuffer();
		if (parent != null) {
			sb.append(parent.getId()).append('$');
		}
		sb.append(path).append('#').append(key);
		SessionUtils.addStateToCacheKey(request, sb);
		
		return sb.toString();
	}

}
