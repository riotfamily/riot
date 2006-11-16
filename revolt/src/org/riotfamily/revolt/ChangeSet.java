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
package org.riotfamily.revolt;

import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.revolt.definition.Database;
import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.LogTable;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public class ChangeSet {

	private String id;

	private int sequenceNumber;
	
	private List refactorings;

	
	public ChangeSet(String id, List refactorings) {
		this.id = id;
		this.refactorings = refactorings; 
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Script getScript(Dialect dialect) {
		Script script = new Script();
		Iterator it = refactorings.iterator();
		while (it.hasNext()) {
			Refactoring refactoring = (Refactoring) it.next();
			script.append(refactoring.getScript(dialect));
		}
		return script;
	}
	
	private boolean isApplied(LogTable logTable) {
		return logTable.containsChangeSet(id, sequenceNumber);
	}
	
	public void markAsApplied(LogTable logTable) {
		logTable.addChangeSet(id, sequenceNumber);
	}

	public void applyToModel(Database model) {
		Iterator it = refactorings.iterator();
		while (it.hasNext()) {
			Refactoring refactoring = (Refactoring) it.next();
			refactoring.alterModel(model);
		}
	}
	
	public void applyIfNeeded(DataSource dataSource, Dialect dialect, 
			LogTable logTable, Database model) {
		
		applyToModel(model);
		if (!isApplied(logTable)) {
			getScript(dialect).execute(dataSource);
			markAsApplied(logTable);			
			if (!DatabaseUtils.databaseMatchesModel(dataSource, model)) {
				throw new DatabaseOutOfSyncException();
			}
		}
		
	}

}
