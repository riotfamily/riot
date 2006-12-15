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

import java.util.Collection;


public interface JobDao {

	public Collection getLogEntries(Long jobId);
	
	public void log(JobLogEntry entry);
	
	public Collection getJobDetails();
	
	public Collection getPendingJobDetails();
	
	public JobDetail getJobDetail(Long jobId);
	
	public JobDetail getPendingJobDetail(String type, String objectId);
	
	public int getAverageStepTime(String type);
	
	public void saveJobDetail(JobDetail detail);
	
	public void updateJobDetail(JobDetail detail);

}
