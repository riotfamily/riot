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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.riotfamily.revolt.definition.Database;
import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.DialectResolver;
import org.riotfamily.revolt.support.LogTable;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public class EvolutionHistory implements BeanNameAware {

	private String moduleName;
	
	private List changeSets;

	private DataSource dataSource;
	
	private Dialect dialect;
	
	private LogTable logTable;
	
	private ArrayList appliedIds;
	
	public EvolutionHistory(DataSource dataSource) {
		this.dataSource = dataSource;
		this.dialect = new DialectResolver().getDialect(dataSource);
	}
	
	public DataSource getDataSource() {
		return this.dataSource;
	}
	
	public Dialect getDialect() {
		return this.dialect;
	}

	public void setBeanName(String name) {
		this.moduleName = name;
	}
	
	public String getModuleName() {
		return this.moduleName;
	}

	public void setChangeSets(ChangeSet[] changeSets) {
		this.changeSets = new ArrayList();
		for (int i = 0; i < changeSets.length; i++) {
			ChangeSet changeSet = changeSets[i];
			changeSet.setHistory(this);
			changeSet.setSequenceNumber(i);
			this.changeSets.add(changeSet);
		}
	}
		
	private Database evolveModel() {
		Database model = new Database();
		Iterator it = changeSets.iterator();
		while (it.hasNext()) {
			ChangeSet changeSet = (ChangeSet) it.next();
			changeSet.alterModel(model);
		}
		return model;
	}
	
	public void init(LogTable logTable) {
		this.logTable = logTable;
		appliedIds = new ArrayList();
		appliedIds.addAll(logTable.getAppliedChangeSetIds(moduleName));
	}
	
	public void evolve() throws DatabaseOutOfSyncException {
		getScript().execute(dataSource);
		DatabaseUtils.validate(dataSource, evolveModel());
	}
	
	public Script getScript() {
		Script script = new Script();
		if (appliedIds.isEmpty()) {
			try {
				DatabaseUtils.validate(dataSource, evolveModel());
				Iterator it = changeSets.iterator();
				while (it.hasNext()) {
					ChangeSet changeSet = (ChangeSet) it.next();
					script.append(markAsApplied(changeSet));
				}
				return script;
			}
			catch (DatabaseOutOfSyncException e) {
			}
		}
		Iterator it = changeSets.iterator();
		while (it.hasNext()) {
			ChangeSet changeSet = (ChangeSet) it.next();
			if (!isApplied(changeSet)) {
				script.append(changeSet.getScript(dialect));
				script.append(logTable.getInsertScript(changeSet));
			}
		}
		return script;
	}
	
	private boolean isApplied(ChangeSet changeSet) {
		if (appliedIds.size() > changeSet.getSequenceNumber()) {
			String appliedId = (String) appliedIds.get(
					changeSet.getSequenceNumber());
			
			if (appliedId.equals(changeSet.getId())) {
				return true;
			}
			throw new DatabaseOutOfSyncException("ChangeSet number " 
					+ changeSet.getSequenceNumber() + " should be [" 
					+ changeSet.getId() + "] but is [" + appliedId + "]");
		}
		return false;
	}
	
	private Script markAsApplied(ChangeSet changeSet) {
		if (changeSet.getSequenceNumber() != appliedIds.size()) {
			throw new DatabaseOutOfSyncException("ChangeSet [" 
					+ changeSet.getId() + "] is number " 
					+ changeSet.getSequenceNumber() + " but there are already "
					+ appliedIds.size() + " ChangeSets applied!");
		}
		appliedIds.add(changeSet.getId());
		return logTable.getInsertScript(changeSet);
	}
		
}
