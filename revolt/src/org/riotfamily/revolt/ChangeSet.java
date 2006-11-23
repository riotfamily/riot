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

import org.riotfamily.revolt.definition.Database;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public class ChangeSet implements Refactoring {

	private EvolutionHistory history;
	
	private String id;

	private int sequenceNumber;
	
	private List refactorings;

	
	public ChangeSet(String id, List refactorings) {
		this.id = id;
		this.refactorings = refactorings; 
	}

	public String getId() {
		return this.id;
	}

	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setHistory(EvolutionHistory history) {
		this.history = history;
	}

	public String getModuleName() {
		return history.getModuleName();
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
	
	public void alterModel(Database model) {
		Iterator it = refactorings.iterator();
		while (it.hasNext()) {
			Refactoring refactoring = (Refactoring) it.next();
			refactoring.alterModel(model);
		}
	}

}
