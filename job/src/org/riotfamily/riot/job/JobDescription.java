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
package org.riotfamily.riot.job;

/**
 * Description created by a Job's {@link Job#setup(String) setup()}-method.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class JobDescription {

	private String name;
	
	private String description;

	private int steps;

	
	public JobDescription() {
	}

	public JobDescription(String name, int steps) {
		this.name = name;
		this.steps = steps;
	}
	
	public JobDescription(String name, String description, int steps) {
		this.name = name;
		this.description = description;
		this.steps = steps;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a String that describes what the job is/will be doing.
	 */
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the number of steps needed to complete the job. This information
	 * is used to provide progress information.
	 */
	public int getSteps() {
		return this.steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
	
}
