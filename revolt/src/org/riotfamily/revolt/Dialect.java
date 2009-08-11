package org.riotfamily.revolt;

import java.util.Collection;

import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.definition.ForeignKey;
import org.riotfamily.revolt.definition.Index;
import org.riotfamily.revolt.definition.RecordEntry;
import org.riotfamily.revolt.definition.Table;
import org.riotfamily.revolt.definition.UniqueConstraint;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public interface Dialect {

	public String getName();
	
	public boolean supports(String databaseProductName, int majorVersion, 
			int minorVersion);

	public Script createTable(Table table);

	public Script renameTable(String name, String renameTo);

	public Script dropTable(String name, boolean cascade);

	public Script addColumn(String table, Column column);

	public Script renameColumn(String table, String name, String renameTo);

	public Script modifyColumn(String table, Column column);

	public Script dropColumn(String table, String name);

	public Script createIndex(String table, Index index);

	public Script dropIndex(String table, String name);

	public Script addUniqueConstraint(String table, UniqueConstraint constraint);

	public Script dropConstraint(String table, String name);

	public Script addForeignKey(String table, ForeignKey fk);

	public Script dropForeignKey(String table, String name);

	public Script insert(String table, Collection<RecordEntry> data);
	
	public Script createAutoIncrementSequence(String name);
	
}
