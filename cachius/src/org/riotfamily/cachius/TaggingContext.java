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
package org.riotfamily.cachius;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Provides static methods to tag cache items.
 */
public class TaggingContext {

	private static final String REQUEST_ATTRIBUTE = 
			TaggingContext.class.getName();
	
	private Log log = LogFactory.getLog(TaggingContext.class);
	
	private TaggingContext parent;
		
	private HashSet tags;
			
	private TaggingContext(TaggingContext parent) {
		this.parent = parent;
	}
	
	public TaggingContext getParent() {
		return this.parent;
	}

	public void addTag(String tag) {
		if (tags == null) {
			tags = new HashSet();
		}
		tags.add(tag);
		if (parent != null) {
			parent.addTag(tag);
		}
		else {
			log.debug("Adding tag: " + tag);
		}
	}
	
	public Set getTags() {
		return this.tags;
	}

	public static void tag(HttpServletRequest request, String tag) {
		Assert.notNull(tag, "Tag must not be null!");
		TaggingContext context = getContext(request);
		if (context != null) {
			context.addTag(tag);
		}
	}
	
	public static void openNestedContext(HttpServletRequest request) {
		TaggingContext parent = getContext(request);
		setContext(request, new TaggingContext(parent));
	}
	
	public static Set popTags(HttpServletRequest request) {
		TaggingContext top = getContext(request);
		if (top != null) {
			setContext(request, top.getParent());
			return top.getTags();
		}
		return null;
	}
	
	public static TaggingContext getContext(HttpServletRequest request) {
		return (TaggingContext) request.getAttribute(REQUEST_ATTRIBUTE);
	}
	
	public static TaggingContext getContext() {
		return (TaggingContext) RequestContextHolder.getRequestAttributes()
				.getAttribute(REQUEST_ATTRIBUTE, 
				RequestAttributes.SCOPE_REQUEST);
	}
	
	private static void setContext(HttpServletRequest request, 
			TaggingContext context) {
		
		request.setAttribute(REQUEST_ATTRIBUTE, context);
	}
	
}
