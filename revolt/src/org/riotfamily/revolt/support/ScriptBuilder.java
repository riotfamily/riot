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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.revolt.support;

import java.util.Iterator;

import org.riotfamily.revolt.Dialect;
import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Database;
import org.riotfamily.revolt.definition.ForeignKey;
import org.riotfamily.revolt.definition.Index;
import org.riotfamily.revolt.definition.Table;
import org.riotfamily.revolt.definition.UniqueConstraint;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ScriptBuilder {

	private Database model;
	
	private Dialect dialect;
	
	private Script script;
	
	public ScriptBuilder(Database model, Dialect dialect) {
		this.model = model;
		this.dialect = dialect;
	}
	
	public Script buildScript() {
		script = new Script();
		Iterator it = model.getSequences().iterator();
		while (it.hasNext()) {
			String sequence = (String) it.next();
			script.append(dialect.createAutoIncrementSequence(sequence));
		}
		it = model.getTables().iterator();
		while (it.hasNext()) {
			Table table = (Table) it.next();
			script.append(dialect.createTable(table));
			createIndices(table);
			createUniqueConstraints(table);
		}
		it = model.getTables().iterator();
		while (it.hasNext()) {
			Table table = (Table) it.next();
			createForeignKeys(table);
		}
		return script;
	}
	
	private void createIndices(Table table) {
		Iterator it = table.getIndices().iterator();
		while (it.hasNext()) {
			Index index = (Index) it.next();
			script.append(dialect.createIndex(table.getName(), index));
		}
	}
	
	private void createUniqueConstraints(Table table) {
		Iterator it = table.getUniqueConstraints().iterator();
		while (it.hasNext()) {
			UniqueConstraint constraint = (UniqueConstraint) it.next();
			script.append(dialect.addUniqueConstraint(table.getName(), constraint));
		}
	}
	
	private void createForeignKeys(Table table) {
		Iterator it = table.getForeignKeys().iterator();
		while (it.hasNext()) {
			ForeignKey fk = (ForeignKey) it.next();
			script.append(dialect.addForeignKey(table.getName(), fk));
		}
	}
}
