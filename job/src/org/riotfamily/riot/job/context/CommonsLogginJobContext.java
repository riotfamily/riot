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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobInterruptedException;

public class CommonsLogginJobContext implements JobContext {
	
	private Log log;
	
	public CommonsLogginJobContext() {
		log = LogFactory.getLog(CommonsLogginJobContext.class);
	}

	public CommonsLogginJobContext(Log log) {
		this.log = log;
	}

	public String getObjectId() {
		return null;
	}

	public void logError(String message) {
		log.error(message);
	}

	public void logInfo(String message) {
		log.info(message);
	}

	public void stepCompleted() throws JobInterruptedException {
	}
	
	public void updateStepsTotal(int stepsTotal) {
	}
	
	public void updateDescription(String description) {
	}

}
