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
package org.riotfamily.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public final class PageValidationUtils {

	private PageValidationUtils() { }


	public static boolean isValidChild(PageNode node, Page page) {
		Collection childs = getChildsWithoutPage(node, page);
		return !containsPathComponent(childs, page.getPathComponent());
	}

	public static boolean isTranslatable(Page page, Locale targetLocale) {
		Collection siblings = getSiblings(page, targetLocale);
		return !PageValidationUtils.containsPathComponent(siblings,
					page.getPathComponent());
	}

	public static boolean containsPathComponent(Collection pages, String pathComponent) {
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (page.getPathComponent().equals(pathComponent)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns all siblings of the page in the given locale. If the locale is
	 * identical to the page's locale the page itself will be contained too.
	 */
	public static Collection getSiblings(Page page, Locale locale) {
		return page.getNode().getParent().getChildPages(locale);
	}

	private static Collection getChildsWithoutPage(PageNode node, Page page) {
		return without(node.getChildPages(page.getLocale()), page);
	}

	private static Collection without(Collection collection, Object item) {
		ArrayList result = new ArrayList(collection);
		result.remove(item);
		return result;
	}


}
