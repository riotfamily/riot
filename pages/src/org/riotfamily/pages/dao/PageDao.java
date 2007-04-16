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
package org.riotfamily.pages.dao;

import java.util.Locale;

import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.PageNode;

/**
 * DAO interface for {@link Page pages} and {@link PageAlias aliases}.
 * <p>
 * Implementors should extend {@link AbstractPageDao} instead of implementing
 * this interface directly.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface PageDao {

	public Page loadPage(Long id);
	
	public Page findPage(PageLocation location);

	public PageAlias findPageAlias(PageLocation location);
	
	public void saveRootPage(Page page, String handlerName);
	
	public void savePage(Page parent, Page child);

	public Page addTranslation(Page page, Locale locale);

	public void updatePage(Page page);

	public void deletePage(Page page);

	public PageNode getRootNode();
	
	public PageNode getNodeForHandler(String handlerName);
	
	public void updateNode(PageNode node);
	
	public void moveNode(PageNode node, PageNode newParent);
	
}