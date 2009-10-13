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
package org.riotfamily.revolt.dialect;

import java.util.HashMap;
import java.util.HashSet;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.definition.Column;
import org.riotfamily.revolt.support.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public abstract class AbstractDialect implements Dialect {

	private Logger log = LoggerFactory.getLogger(AbstractDialect.class);
	
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
		String type = nativeTypes.get(column.getType());
		if (type == null) {
			throw new TypeNotSupportedException(column.getType());
		}
		if (column.isLengthSet() && typeHasLength(type)) {
			type += "(" + column.getLength() + ")";
		}
		return type;
	}
	
}
