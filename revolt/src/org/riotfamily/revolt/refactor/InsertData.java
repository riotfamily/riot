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
