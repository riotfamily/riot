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
 *   alf
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.dao.cleanup;

import java.text.ParseException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

public class DelayedContentCleanupTriggerBean extends SimpleTriggerBean
	implements InitializingBean {
	
	private static final long serialVersionUID = -1043318234095263669L;

	private long delay;

	@Override
	public void afterPropertiesSet() throws ParseException {
		setRepeatCount(-1);
		setRepeatInterval(delay * 500);
		setStartDelay(60 * 1000);
		
		super.afterPropertiesSet();
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
}
