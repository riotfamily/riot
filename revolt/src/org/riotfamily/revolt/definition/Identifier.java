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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.revolt.definition;


/**
 * Abstract base for all classes that represent a named component within 
 * a database definition, like tables, columns and constraints.
 * 
 * @see DefinitionUtils
 * @author Felix Gnass <fgnass@neteye.de>
 */
public class Identifier {

	public static final String QUOTED_DELIMITER = "`";

	private String name;
	
	private boolean quoted;
	
	public Identifier() {
	}

	public Identifier(String name) {
		setName(name);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if (name != null && name.startsWith(QUOTED_DELIMITER) 
				&& name.endsWith(QUOTED_DELIMITER)) {
			
			quoted = true;
			name = name.replace(QUOTED_DELIMITER, "");
		}
		this.name = name;
	}

	public boolean isQuoted() {
		return this.quoted;
	}

	public void setQuoted(boolean quoted) {
		this.quoted = quoted;
	}
	
	public int hashCode() {
		return getName() == null ? 0 : getName().toUpperCase().hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (name == null) {
			return false;
		}
		if (!(obj instanceof Identifier)) {
			return false;
		}
		Identifier other = (Identifier) obj;
		return name.equals(other.name) || (quoted == other.quoted 
				&& name.equalsIgnoreCase(other.name));  
	}

	public String toString() {
		return name;
	}
	
}
