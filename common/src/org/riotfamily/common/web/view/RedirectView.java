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
package org.riotfamily.common.web.view;

import java.util.Collections;
import java.util.Map;

/**
 * Extension to Spring's RedirectView that can be configured to not append
 * the model as query-string.
 * 
 * @deprecated Because we don't really need it.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class RedirectView extends 
		org.springframework.web.servlet.view.RedirectView {

	private boolean appendModelAsParams = false;
	
	public RedirectView() {
	}
	
	public RedirectView(String url) {
		super(url);
	}

	public RedirectView(String url, boolean contextRelative, 
			boolean http10Compatible) {
		
		super(url, contextRelative, http10Compatible);
	}

	public void setAppendModelAsParams(boolean appendModelAsParams) {
		this.appendModelAsParams = appendModelAsParams;
	}

	protected Map queryProperties(Map model) {
		if (appendModelAsParams && model != null) {
			return super.queryProperties(model);
		}
		return Collections.EMPTY_MAP;
	}

}
