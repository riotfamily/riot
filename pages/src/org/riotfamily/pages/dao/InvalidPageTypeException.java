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
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.riot.dao.RiotDaoException;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 8.0
 */
public class InvalidPageTypeException extends RiotDaoException {
	
	private static final String CODE_NAME = "invalidPageType";
	
	private static final String DEFAULT_MESSAGE = "The defined hierarchy " +
			"does not allow this page here.";

	public InvalidPageTypeException(PageNode parentNode, Page page) {
		super(CODE_NAME, new String[] { parentNode.getPageType(), page.getPageType() }, DEFAULT_MESSAGE);
	}

}
