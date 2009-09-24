/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.riot.job.context;

import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobInterruptedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiotLogJobContext implements JobContext {
	
	private Logger log;
	
	private int stepsTotal;
	
	private int stepsCompleted;
	
	public RiotLogJobContext() {
		log = LoggerFactory.getLogger(RiotLogJobContext.class);
	}

	public RiotLogJobContext(Logger log) {
		this.log = log;
	}

	public String getObjectId() {
		return null;
	}

	public void logError(String message) {
		log.warn(message);
	}

	public void logInfo(String message) {
		if (stepsTotal > 0) {
			message = "[" + (stepsCompleted * 100 / stepsTotal) + "%] " + message;
		}
		log.info(message);
	}

	public void stepCompleted() throws JobInterruptedException {
		stepsCompleted++;
	}
	
	public void updateStepsTotal(int stepsTotal, boolean resetStepsCompleted) {
		this.stepsTotal = stepsTotal;
		if (resetStepsCompleted) {
			stepsCompleted = 0;
		}
	}
	
	public void updateDescription(String description) {
	}

}
