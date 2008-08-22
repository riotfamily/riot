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
package org.riotfamily.dbmsgsrc.support;

import org.riotfamily.common.i18n.CodeRevealingMessageSource;
import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.springframework.context.MessageSource;

public class RiotDbMessageSource extends CodeRevealingMessageSource {

	private DbMessageSource dbMessageSource;
	
	public RiotDbMessageSource(DbMessageSourceDao dao) {
		dbMessageSource = new DbMessageSource(dao);
		dbMessageSource.setBundle("riot");
		super.setParentMessageSource(dbMessageSource);
	}
	
	@Override
	public void setParentMessageSource(MessageSource parent) {
		dbMessageSource.setParentMessageSource(parent);
	}
	
	@Override
	protected String revealCode(String message, String code) {
		StringBuffer sb = new StringBuffer();
		if (message != null) {
			sb.append(message);
		}
		sb.append("<span class=\"messageCode\" onclick=\"window.open('")
			.append(getEditorUrl(code)).append("', 'dbmsgsrc')\"></span>");
		
		return sb.toString();
	}

	private String getEditorUrl(String code) {
		return "about:blank";
	}
}
