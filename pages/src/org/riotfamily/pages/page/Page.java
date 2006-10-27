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
package org.riotfamily.pages.page;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.member.WebsiteMember;


public interface Page {

	/**
	 * Returns the full path of the page. The path starts with a slash and
	 * consits of the pathComponents of all ancestor pages, delimited by slashes.
	 * Note that neither the context path nor the extension (to which the 
	 * website-servlet is mapped) is included within the path.
	 */
	public String getPath();

	/**
	 * Returns a string that is used to construct the path.
	 */
	public String getPathComponent();
	
	public String getTitle();
	
	public String getDescription();
	
	public String getKeywords();
	
	/**
	 * Returns if the page is accessible and therefore should be included in
	 * the navigation. Implementors should not check the published flag, which
	 * is tested seperately. If a non-accessible page is requested, a 403 error
	 * code (forbidden) is sent to the client.
	 */
	public boolean isAccessible(HttpServletRequest request, WebsiteMember member);	
	
	/**
	 * Returns the parent page, or <code>null</code> if the page is a 
	 * top-level page.
	 */
	public Page getParent();
	
	/**
	 * Sets the parent page.
	 */
	public void setParent(Page parent);
	
	/**
	 * Called after the pathComponent or the parent has been modified. 
	 * Implementors must rebuild the page's path in order to reflect the changes.
	 */
	public void updatePath();

	/**
	 * Returns an integer value that is used to sort the pages. The values 
	 * don't need to be continous, but should be unique among siblings.
	 */
	public int getPosition();
	
	/**
	 * Returns the page's children aka sub-pages.
	 */
	public Collection getChildPages();
	
	public void addChildPage(Page child);
	
	public void removeChildPage(Page child);
	
	/**
	 * Returns the name of the controller that should be used to display the 
	 * page.
	 */
	public String getControllerName();
	
	/**
	 * Returns whether the page acts as a folder. When a folder is requested,
	 * a redirect to the first accessible child-page is sent to the client.
	 */
	public boolean isFolder();
	
	/**
	 * Returns whether the page should be exluded from the navigation. In 
	 * contrast to non-accessible pages a request to a hidden page will not
	 * result in an error.
	 */
	public boolean isHidden();
	
	/**
	 * Returns whether a page is published. Unpublished pages a only visible
	 * to Riot users, all other requests will result in an 404 (not found) error.
	 */
	public boolean isPublished();
	
	/**
	 * Sets whether the page is published.
	 */
	public void setPublished(boolean published);
	
	/**
	 * Returns whether the page is a system-page an should not be editable by
	 * Riot users.
	 */
	public boolean isSystemPage();
	
	public boolean isNew();

}
