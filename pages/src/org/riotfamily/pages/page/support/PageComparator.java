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
package org.riotfamily.pages.page.support;

import java.util.Comparator;

import org.riotfamily.pages.page.Page;

/**
 * Comparator that compares two pages by looking at their positions.
 */
public class PageComparator implements Comparator {

	public static final PageComparator INSTANCE = new PageComparator();

	public int compare(Object o1, Object o2) {
		if (o1 instanceof Page && o2 instanceof Page) {
			Page p1 = (Page) o1;
			Page p2 = (Page) o2;
			return p1.getPosition() - p2.getPosition();
		}
		else {
			throw new IllegalArgumentException("Arguments must both be Pages");
		}
	}

}
