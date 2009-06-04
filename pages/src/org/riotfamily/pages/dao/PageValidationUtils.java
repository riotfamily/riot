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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.dao;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.SiteMapItem;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public final class PageValidationUtils {

	private PageValidationUtils() { }


	public static boolean isValidChild(SiteMapItem parent, Page page) {
		for (Page child : parent.getChildPages()) {
			if (!child.equals(page) && child.getPathComponent().equals(page.getPathComponent())) {
				return false;
			}
		}
		return true;
	}

	/*
	public static boolean isTranslatable(Page page, Site targetSite) {
		Collection<Page> siblings = getSiblings(page, targetSite);
		return !PageValidationUtils.containsPathComponent(siblings,
					page.getPathComponent());
	}

	public static boolean containsPathComponent(Collection<Page> pages, String pathComponent) {
		for (Page page : pages) {
			if (page.getPathComponent().equals(pathComponent)) {
				return true;
			}
		}
		return false;
	}

	public static Collection<Page> getSiblings(Page page, Site site) {
		return page.getNode().getParent().getChildPages(site);
	}

	private static Collection<Page> getChildsWithoutPage(PageNode node, Page page) {
		return without(node.getChildPagesWithFallback(page.getSite()), page);
	}

	private static Collection<Page> without(Collection<Page> collection, Object item) {
		ArrayList<Page> result = new ArrayList<Page>(collection);
		result.remove(item);
		return result;
	}
	*/

}
