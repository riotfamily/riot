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
package org.riotfamily.pages.riot.command;

import java.util.Locale;

import org.riotfamily.pages.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.springframework.util.ObjectUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class PageCommandUtils {

	private PageCommandUtils() {
	}

	public static Page getPage(CommandContext context) {
		return (Page) context.getBean();
	}

	public static Locale getParentLocale(CommandContext context) {
		Object parent = context.getParent();
		Locale locale = null;
		if (parent instanceof Page) {
			locale = ((Page) parent).getLocale();
		}
		else if (parent instanceof Locale) {
			locale = (Locale) parent;
		}
		return locale;
	}

	public static boolean isTranslated(CommandContext context) {
		Page page = getPage(context);
		Locale locale = getParentLocale(context);
		return ObjectUtils.nullSafeEquals(page.getLocale(), locale);
	}

	public static boolean isSystemPage(CommandContext context) {
		return getPage(context).getNode().isSystemNode();
	}
}
