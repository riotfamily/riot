package org.riotfamily.revolt.dialect;

import java.util.HashMap;
import java.util.HashSet;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.support.TypeMap;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public abstract class AbstractDialect implements Dialect {

	private RiotLog log = RiotLog.get(AbstractDialect.class);
	
	private HashMap<String, String> nativeTypes = new HashMap<String, String>();
	
	private HashSet<String> typesWithLength = new HashSet<String>();

	public AbstractDialect() {
		registerTypes();
		if (!TypeMap.isComplete(nativeTypes)) {
			log.warn("Dialect does not support all JDBC types!");
		}
	}
	
	public String getName() {
		String s = getClass().getName();
		s = s.substring(s.lastIndexOf('.') + 1);
		return s.substring(0, s.indexOf("Dialect"));
	}

	protected abstract void registerTypes();
	
	protected final void registerType(String jdbcName, String nativeName) {
		registerType(jdbcName, nativeName, false);
	}
	
	protected final void registerType(String jdbcName, String nativeName, 
			boolean hasLength) {
		
		nativeTypes.put(jdbcName, nativeName);
		if (hasLength) {
			typesWithLength.add(jdbcName);
		}
	}
	
	protected boolean typeHasLength(String type) {
		return typesWithLength.contains(type);
	}

	protected final String getColumnType(Column column) {
		String type = (String) nativeTypes.get(column.getType());
		if (type == null) {
			throw new TypeNotSupportedException(column.getType());
		}
		if (column.isLengthSet() && typeHasLength(type)) {
			type += "(" + column.getLength() + ")";
		}
		return type;
	}
	
}
