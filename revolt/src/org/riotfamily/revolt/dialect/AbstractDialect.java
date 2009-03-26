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
