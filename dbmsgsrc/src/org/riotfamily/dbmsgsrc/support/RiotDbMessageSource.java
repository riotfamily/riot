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

import java.util.Set;

import org.riotfamily.common.i18n.CodeRevealingMessageSource;
import org.riotfamily.common.util.Generics;
import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.auth.RiotUser;
import org.springframework.context.MessageSource;

public class RiotDbMessageSource extends CodeRevealingMessageSource 
		implements RiotRuntimeAware {

	private DbMessageSource dbMessageSource;
	
	private RiotRuntime runtime;
	
	private Set<String> revealTo = Generics.newHashSet();
	
	public RiotDbMessageSource(DbMessageSourceDao dao) {
		dbMessageSource = new DbMessageSource(dao);
		dbMessageSource.setBundle("riot");
		super.setParentMessageSource(dbMessageSource);
	}
	
	@Override
	public void setParentMessageSource(MessageSource parent) {
		dbMessageSource.setParentMessageSource(parent);
	}
	
	public void setRiotRuntime(RiotRuntime runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public boolean isRevealCodes() {
		RiotUser user = AccessController.getCurrentUser();
		if (user != null) {
			return revealTo.contains(user.getUserId());
		}
		return false;
	}

	@Override
	public void setRevealCodes(boolean revealCodes) {
		RiotUser user = AccessController.getCurrentUser();
		if (user != null) {
			if (revealCodes) {
				revealTo.add(user.getUserId());
			}
			else {
				revealTo.remove(user.getUserId());
			}
		}
	}
	
	@Override
	protected String revealCodes(String message, String... codes) {
		String url = getEditorUrl(codes[0]);
		if (url != null) {
			StringBuilder sb = new StringBuilder();
			if (message != null) {
				sb.append(message);
			}
			sb.append("<span class=\"messageCode\" onclick=\"window.open('")
				.append(url).append("','dbmsgsrc','width=650,height=400');return false\"></span>");
			
			return sb.toString();
		}
		return message;
	}

	private String getEditorUrl(String code) {
		MessageBundleEntry entry = dbMessageSource.getEntry(code, null);
		if (entry != null) {
			return getContextPath() + runtime.getUrl("popupFormController", "riotMessageBundleEntry", entry.getId());
		}
		return null;
	}
}
