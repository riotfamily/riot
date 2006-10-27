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
package org.riotfamily.riot.job.support;

import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobDescription;

public class TestJob implements Job {

	private String name = "Test Job";
	
	private String description;
	
	private int steps = 50;
	
	private long delay = 1000;
	
	private int errorAfter = 0;
	
	private boolean reportProgress = true;
	
	private int step = 1;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setReportProgress(boolean reportProgress) {
		this.reportProgress = reportProgress;
	}

	public void setErrorAfter(int errorAfter) {
		this.errorAfter = errorAfter;
	}

	public JobDescription setup(String objectId) {
		return new JobDescription(name, description, reportProgress ? steps : 0);
	}
	
	public void execute(JobContext context) {
		while (step <= steps) {
			context.logInfo("Performing step " + step++);
			if (errorAfter > 0 && step % errorAfter == 0) {
				context.logError("A random error occured in step " + step);
			}
			try {
				Thread.sleep(delay);
			}
			catch (InterruptedException e) {
			}
			context.stepCompleted();
		}
	}

	public boolean isConcurrent() {
		return false;
	}

	public void tearDown(String objectId) {
		step = 1;
	}

}
