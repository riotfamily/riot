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
package org.riotfamily.revolt;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class ChangeSet implements Refactoring {

	private EvolutionHistory history;
	
	private String id;

	private int sequenceNumber;
	
	private List<Refactoring> refactorings;

	
	public ChangeSet(String id, List<Refactoring> refactorings) {
		this.id = id;
		this.refactorings = refactorings; 
	}

	public String getId() {
		return this.id;
	}

	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setHistory(EvolutionHistory history) {
		this.history = history;
	}

	public String getModuleName() {
		return history.getModuleName();
	}
	
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Script getScript(Dialect dialect, JdbcTemplate template) {
		try {
			Script script = new Script();
			for (Refactoring refactoring : refactorings) {
				Script s = refactoring.getScript(dialect, template);
				if (s != null) {
					script.append(s);
				}
			}
			return script;
		}
		catch (Exception e) {
			throw new EvolutionException(e);
		}
	}
	
}
