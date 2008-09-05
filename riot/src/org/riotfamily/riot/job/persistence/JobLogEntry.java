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
package org.riotfamily.riot.job.persistence;

import java.util.Date;

import org.riotfamily.common.util.FormatUtils;


public class JobLogEntry {

	public static final int INFO = 1;
	
	public static final int ERROR = 2;
	
	private Long id;
	
	private JobDetail job;
	
	private Date date;
	
	private int priority;
	
	private String message;

	public JobLogEntry() {
	}
	
	public JobLogEntry(JobDetail job, String message) {
		this(job, INFO, message);
	}

	public JobLogEntry(JobDetail job, int priority, String message) {
		this.job = job;
		this.priority = priority;
		this.message = FormatUtils.truncate(message, 255);
		this.date = new Date();
	}

	public Long getId() {
		return this.id;
	}

	public Long getJobId() {
		return job.getId();
	}
	
	public Date getDate() {
		return this.date;
	}

	public String getMessage() {
		return this.message;
	}

	public int getPriority() {
		return this.priority;
	}

}
