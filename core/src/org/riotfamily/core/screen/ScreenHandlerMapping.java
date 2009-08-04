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
package org.riotfamily.core.screen;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.mapping.ReverseHandlerMapping;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.core.security.AccessController;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

public class ScreenHandlerMapping extends AbstractHandlerMapping
		implements ReverseHandlerMapping {
	
	private ScreenRepository repository;
	
	public ScreenHandlerMapping(ScreenRepository repository) {
		this.repository = repository;
	}

	@Override
	protected Object getHandlerInternal(HttpServletRequest request)
			throws Exception {
		
		String path = ServletUtils.getPathWithoutServletMapping(request);
		String[] s = StringUtils.tokenizeToStringArray(path, "/");
		
		if (!StringUtils.hasLength(path) || path.equals("/") 
				|| "screen".equals(stringAt(s, 0))) {
			
			String screenId = stringAt(s, 1);
			String objectId = stringAt(s, 2);
			String parentId = null;
			boolean parentIsNode = false;
			if ("-".equals(objectId)) {
				objectId = null;
				parentId = stringAt(s, 3);
				String parentScreenId = stringAt(s, 4);
				parentIsNode = screenId.equals(parentScreenId);
			}
			
			RiotScreen screen = repository.getScreen(screenId);
			if (AccessController.isGranted("view", screen)) {
				ScreenContext context = new ScreenContext(
						screen, request, objectId, parentId, parentIsNode);
				context.expose();
			} else {
				screen = null;
			}
			return screen;
		}
		
		return null;
	}
	
	private static String stringAt(String[] a, int i) {
		if (a != null && a.length > i) {
			return a[i];
		}
		return null;
	}

	public String getUrlForHandler(String handlerName, Object attributes, 
			HttpServletRequest request) {

		if (attributes instanceof ScreenContext) {
			ScreenContext context = (ScreenContext) attributes;
			StringBuilder path = new StringBuilder("/screen/");
			path.append(handlerName).append('/');
			if (context.getObjectId() != null) {
				path.append(context.getObjectId());
			}
			else if (context.getParentId() != null) {
				path.append("-/").append(context.getParentId());
				if (context.isNestedTreeItem()) {
					path.append('/').append(handlerName);	
				}
			}
			return path.toString();
		}
		return null;
	}
}
