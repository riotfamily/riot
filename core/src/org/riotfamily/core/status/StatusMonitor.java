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
package org.riotfamily.core.status;

import java.util.Map;

import org.riotfamily.core.screen.ModelAndViewScreenlet;
import org.riotfamily.core.screen.ScreenContext;

public abstract class StatusMonitor extends ModelAndViewScreenlet {

	@Override
	protected Class<?> getTemplateClass() {
		return StatusMonitor.class;
	}
	
	@Override
	protected final void populateModel(Map<String, Object> model,
			ScreenContext context) {
		
		model.put("status", getStatus(context));
	}
	
	protected abstract Status getStatus(ScreenContext context);

}
