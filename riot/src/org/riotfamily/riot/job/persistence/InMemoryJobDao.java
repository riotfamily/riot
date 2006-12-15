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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 *
 */
public class InMemoryJobDao implements JobDao {

	private HashMap jobDetails = new HashMap();
	
	private HashMap jobLogEntries = new HashMap();
	
	public int getAverageStepTime(String type) {
		return 0;
	}

	public JobDetail getJobDetail(Long jobId) {
		return (JobDetail) jobDetails.get(jobId);
	}

	public Collection getJobDetails() {
		return jobDetails.values();
	}

	public Collection getLogEntries(Long jobId) {
		return (Collection) jobLogEntries.get(jobId);
	}

	public JobDetail getPendingJobDetail(String type, String objectId) {
		return null;
	}

	public Collection getPendingJobDetails() {
		return null;
	}

	public synchronized void log(JobLogEntry entry) {
		ArrayList entries = (ArrayList) jobLogEntries.get(entry.getJobId());
		if (entries == null) {
			entries = new ArrayList();
			jobLogEntries.put(entry.getJobId(), entries);
		}
		entries.add(entry);
	}

	public synchronized void saveJobDetail(JobDetail detail) {
		detail.setId(new Long(jobDetails.size() + 1));
		jobDetails.put(detail.getId(), detail);
	}

	public void updateJobDetail(JobDetail detail) {
	}

}
