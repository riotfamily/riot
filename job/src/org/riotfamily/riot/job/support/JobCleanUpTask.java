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
package org.riotfamily.riot.job.support;

import org.riotfamily.common.scheduling.TransactionalScheduledTask;
import org.riotfamily.riot.job.dao.JobDao;
import org.springframework.transaction.PlatformTransactionManager;

public class JobCleanUpTask extends TransactionalScheduledTask {

	private JobDao dao;
	
	public JobCleanUpTask(PlatformTransactionManager tx, JobDao dao) {
		super(tx);
		this.dao = dao;
	}

	@Override
	protected void executeInTransaction() throws Exception {
		dao.deleteObsoleteJobDetails();
	}
	
}
