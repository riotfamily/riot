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

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.Assert;

/**
 * Provides static methods to tag cache items.
 */
public class CachiusContext {
	
	private static ThreadLocal<CachiusContext> currentContext = new ThreadLocal<CachiusContext>();

	private CachiusContext parent;

	private HashSet<String> tags;
	
	private HashSet<File> involvedFiles;
	
	private long timeToLive = -1;
	
	private boolean preventCaching;

	/**
	 * Private constructor that creates a nested context.
	 */
	private CachiusContext(CachiusContext parent) {
		this.parent = parent;
	}

	/**
	 * Returns the parent context, or <code>null</code> if it is the root
	 * context.
	 */
	public CachiusContext getParent() {
		return this.parent;
	}

	/**
	 * Adds the given tag.
	 * @throws IllegalArgumentException if the tag is <code>null</code>
	 */
	public void addTag(String tag) {
		Assert.notNull(tag, "Tag must not be null.");
		if (tags == null) {
			tags = new HashSet<String>();
		}
		tag = tag.intern();
		tags.add(tag);
	}
	
	public void addTags(Collection<String> tags) {
		if (tags != null) {
			for (String tag : tags) {
				addTag(tag);
			}
		}
	}
	
	public boolean hasTag(String tag) {
		return tags != null && tags.contains(tag);
	}
	
	/**
	 * Adds the given file.
	 * @throws IllegalArgumentException if the tag is <code>null</code>
	 */
	public void addInvolvedFile(File file) {
		Assert.notNull(file, "File must not be null.");
		if (involvedFiles == null) {
			involvedFiles = new HashSet<File>();
		}
		involvedFiles.add(file);
	}
	
	public void addInvolvedFiles(Collection<File> files) {
		if (files != null) {
			for (File file : files) {
				addInvolvedFile(file);
			}
		}
	}
	
	/**
	 * Sets whether caching should be prevented, i.e. the CacheItem should
	 * be discarded.
	 */
	public void setPreventCaching(boolean preventCaching) {
		if (preventCaching) {
			this.preventCaching = true;
		}
	}
	
	public boolean isPreventCaching() {
		return preventCaching;
	}

	/**
	 * Returns the tags assigned via the {@link #addTag(String)} method.
	 */
	public Set<String> getTags() {
		return this.tags;
	}
	
	public HashSet<File> getInvolvedFiles() {
		return involvedFiles;
	}
	
	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long ttl) {
		if (timeToLive == -1 || ttl < timeToLive) {
			timeToLive = ttl;
		}
	}
	
	/**
	 * Closes the context making its parent the new current context. 
	 */
	public void close() {
		currentContext.set(parent);
	}
	
	
	// -- Static methods ------------------------------------------------------

	
	public static void tag(String tag) {
		CachiusContext context = getContext();
		if (context != null) {
			context.addTag(tag);
		}
	}
	
	public static void addFile(File file) {
		CachiusContext context = getContext();
		if (context != null) {
			context.addInvolvedFile(file);
		}
	}
	
	public static void preventCaching() {
		CachiusContext context = getContext();
		if (context != null) {
			context.setPreventCaching(true);
		}
	}
	
	public static void expireIn(long millis) {
		CachiusContext context = getContext();
		if (context != null) {
			context.setTimeToLive(millis);
		}
	}
	
	/**
	 * Opens a nested context.
	 */
	public static CachiusContext openNestedContext() {
		CachiusContext parent = currentContext.get();
		CachiusContext context = new CachiusContext(parent);
		currentContext.set(context);
		return context;
	}

	/**
	 * Retrieves the context for the current thread. The method will 
	 * return <code>null</code> if no open context exists.
	 */
	public static CachiusContext getContext() {
		return currentContext.get();
	}

	static void bubbleUp(CacheItem cacheItem) {
		if (cacheItem != null) {
			CachiusContext context = getContext();
			if (context != null) {
				context.addTags(cacheItem.getTags());
				context.addInvolvedFiles(cacheItem.getInvolvedFiles());
				context.setTimeToLive(cacheItem.getTimeToLive());
			}
		}
	}
	
}
