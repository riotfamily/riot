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
package org.riotfamily.revolt.refactor;

import java.util.List;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.Table;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class CreateTable implements Refactoring {

	private String name;
	
	private List<Column> columns;

	public CreateTable() {
	}
	
	public CreateTable(String name, List<Column> columns) {
		this.name = name;
		this.columns = columns;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Script getScript(Dialect dialect, JdbcTemplate template) {
		return dialect.createTable(new Table(name, columns));
	}

}
