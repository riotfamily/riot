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
package org.riotfamily.pages;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Page {

	private Long id;
	
	private PageNode node;
	
	private Locale locale;
	
	private String pathComponent;

	private String path;
	
	private Date creationDate;
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public PageNode getNode() {
		return this.node;
	}
	
	public void setNode(PageNode node) {
		this.node = node;
	}

	public Locale getLocale() {
		return this.locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getPathComponent() {
		return pathComponent;
	}

	public void setPathComponent(String pathComponent) {
		this.pathComponent = pathComponent;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Page getParentPage() {
		PageNode parentNode = node.getParent();
		if (parentNode == null) {
			return null;
		}
		return parentNode.getPage(locale);
	}
	
	public Collection getChildPages() {
		return getChildPages(false);
	}
	
	public Collection getChildPages(boolean fallback) {
		LinkedList childPages = new LinkedList();
		Iterator it = node.getChildNodes().iterator();
		while (it.hasNext()) {
			PageNode childNode = (PageNode) it.next();
			childPages.add(childNode.getPage(locale, fallback));
		}
		return Collections.unmodifiableCollection(childPages);
	}
	
	public void addChildPage(Page child) {
		child.setLocale(locale);
		node.addChildNode(new PageNode(child));
	}
	
	public String getHandlerName() {
		return node.getHandlerName();
	}
	
	public String toString() {
		return locale + ":" + path;
	}
}
