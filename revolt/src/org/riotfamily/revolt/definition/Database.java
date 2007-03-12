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
package org.riotfamily.revolt.definition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 *
 */
public class Database {

	private List tables = new ArrayList();
	
	private Set sequences = new HashSet();
	
	public List getTables() {
		return this.tables;
	}

	public void addTable(Table table) {
		tables.add(table);
	}
	
	public Table getTable(String name) {
		return (Table) tables.get(tables.indexOf(new Identifier(name)));
	}
	
	public void removeTable(String name) {
		tables.remove(new Identifier(name));
	}

	public void addSequence(String name) {
		sequences.add(name);
	}

	public Set getSequences() {
		return this.sequences;
	}

}
