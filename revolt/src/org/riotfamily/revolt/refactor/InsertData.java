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
package org.riotfamily.revolt.refactor;

import java.util.ArrayList;
import java.util.List;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Refactoring;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.RecordEntry;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class InsertData implements Refactoring {

	private String table;
	
	private List<RecordEntry> entries;

	public InsertData() {
	}
	
	
	public InsertData(String table) {
		this.table = table;
		this.entries = new ArrayList<RecordEntry>();
	}

	public InsertData(String table, List<RecordEntry> entries) {
		this.table = table;
		this.entries = entries;
	}

	public void setEntries(List<RecordEntry> entries) {
		this.entries = entries;
	}
	
	public void addEntry(RecordEntry entry) {
		entries.add(entry);
	}
	
	public void addEntry(String column, Object value) {
		String s;
		if (value == null) {
			s = "NULL";
		}
		else {
			s = StringUtils.quoteIfString(value).toString();
		}
		entries.add(new RecordEntry(column, s));
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Script getScript(Dialect dialect, SimpleJdbcTemplate template) {
		return dialect.insert(table, entries);
	}

}
