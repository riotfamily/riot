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

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.StringUtils;

public class ExecSql implements Refactoring {

	private String sql;

	public ExecSql(String sql) {
		this.sql = sql;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template)
			throws Exception {
	
		Script script = new Script();
		String[] statements = StringUtils.delimitedListToStringArray(sql, ";");
		for (String s : statements) {
			s = s.trim();
			if (s.length() > 0) {
				script.append(s);
				script.newStatement();
			}
		}
		return script;
	}

}
