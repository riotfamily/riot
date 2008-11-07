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
import java.util.Iterator;
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

	private static final String REQUEST_ATTRIBUTE = TaggingContext.class.getName();

	private Log log = LogFactory.getLog(TaggingContext.class);

	private HttpServletRequest request;
	
	private TaggingContext parent;

	private HashSet tags;
	
	private boolean preventCaching;

	/**
	 * Private constructor that creates a nested context.
	 */
	private TaggingContext(HttpServletRequest request, TaggingContext parent) {
		this.request = request;
		this.parent = parent;
	}

	/**
	 * Returns the parent context, or <code>null</code> if it is the root
	 * context.
	 */
	public TaggingContext getParent() {
		return this.parent;
	}

	/**
	 * Adds the given tag. If the context is a nested context, the ancestors are
	 * also tagged.
	 * 
	 * @throws IllegalArgumentException if the tag is <code>null</code>
	 */
	public void addTag(String tag) {
		Assert.notNull(tag, "Tag must not be null.");
		if (tags == null) {
			tags = new HashSet();
		}
		tags.add(tag);
		if (parent != null) {
			parent.addTag(tag);
		}
		else {
			if (log.isDebugEnabled()) {
				log.debug("Adding tag: " + tag);
			}
		}
	}

	/**
	 * Sets whether caching should be prevented, i.e. the CacheItem should
	 * be discarded.
	 */
	public void setPreventCaching(boolean preventCaching) {
		this.preventCaching = preventCaching;
		if (parent != null) {
			parent.setPreventCaching(preventCaching);
		}
	}
	
	public boolean isPreventCaching() {
		return preventCaching;
	}

	/**
	 * Returns the tags assigned via the {@link #addTag(String)} method.
	 */
	public Set getTags() {
		return this.tags;
	}
	
	/**
	 * Closes the context making its parent the new current context. 
	 */
	public void close() {
		request.setAttribute(REQUEST_ATTRIBUTE, parent);
	}
	
	// -- Static methods ------------------------------------------------------

	
	public static void tag(HttpServletRequest request, String tag) {
		TaggingContext context = getContext(request);
		if (context != null) {
			context.addTag(tag);
		}
	}
	
	public static void tag(String tag) {
		TaggingContext context = getContext();
		if (context != null) {
			context.addTag(tag);
		}
	}
	
	public static void preventCaching(HttpServletRequest request) {
		TaggingContext context = getContext(request);
		if (context != null) {
			context.setPreventCaching(true);
		}
	}
	
	public static void preventCaching() {
		TaggingContext context = getContext();
		if (context != null) {
			context.setPreventCaching(true);
		}
	}

	public static void inheritFrom(CacheItem cacheItem) {
		if (cacheItem != null) {
			TaggingContext context = getContext();
			if (context != null) {
				if (cacheItem.getTags() != null) {
					Iterator it = cacheItem.getTags().iterator();
					while (it.hasNext()) {
						context.addTag((String) it.next());
					}
				}
				context.setPreventCaching(cacheItem.isNew());
			}
		}
	}
	
	/**
	 * Opens a nested context.
	 */
	public static TaggingContext openNestedContext(HttpServletRequest request) {
		TaggingContext parent = getContext(request);
		TaggingContext context = new TaggingContext(request, parent);
		request.setAttribute(REQUEST_ATTRIBUTE, context);
		return context;
	}

	/**
	 * Retrieves the current context from the given request. The method will 
	 * return <code>null</code> if no open context exists.
	 */
	public static TaggingContext getContext(HttpServletRequest request) {
		return (TaggingContext) request.getAttribute(REQUEST_ATTRIBUTE);
	}

	/**
	 * Retrieves the current context using Spring's 
	 * {@link RequestContextHolder}. 
	 */
	public static TaggingContext getContext() {
		return (TaggingContext) RequestContextHolder.getRequestAttributes()
				.getAttribute(REQUEST_ATTRIBUTE, 
				RequestAttributes.SCOPE_REQUEST);
	}

}
