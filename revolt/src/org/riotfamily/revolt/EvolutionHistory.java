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
package org.riotfamily.revolt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.revolt.definition.Database;
import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.DialectResolver;
import org.riotfamily.revolt.support.LogTable;
import org.riotfamily.revolt.support.ScriptBuilder;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * 
 */
public class EvolutionHistory implements BeanNameAware {

	private static final Log log = LogFactory.getLog(EvolutionHistory.class);
	
	private String moduleName;
	
	private List changeSets;

	private DataSource dataSource;
	
	private Dialect dialect;
	
	private LogTable logTable;
	
	private ArrayList appliedIds;
	
	private boolean newModule;
	
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

	public void validate() {
		DatabaseUtils.validate(dataSource, evolveModel());
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
	
	/**
	 * Initializes the history from the given log-table.
	 */
	public void init(LogTable logTable) {
		this.logTable = logTable;
		appliedIds = new ArrayList();
		appliedIds.addAll(logTable.getAppliedChangeSetIds(moduleName));
	}

	/**
	 * Returns a script that needs to be executed in order update the schema.
	 */
	public Script getScript() {
		if (appliedIds.isEmpty()) {
			log.info("The log-table contains no entries for module '" 
					+ moduleName + "'. Checking if schema is up-to-date ...");
			
			newModule = true;
			return getEvolvedSetupScript();
		}
		else {
			return getMigrationScript();
		}
	}
	
	private Script getEvolvedSetupScript() {
		Script script = new Script();
		Database model = evolveModel();
		try {
			DatabaseUtils.validate(dataSource, model);
			log.info("Schema looks okay. Marking all changes as applied.");
		}
		catch (DatabaseOutOfSyncException e) {
			log.info(e.getMessage());
			log.info("Generating setup script ...");
			ScriptBuilder builder = new ScriptBuilder(model, dialect);
			script.append(builder.buildScript());
		}
		Iterator it = changeSets.iterator();
		while (it.hasNext()) {
			ChangeSet changeSet = (ChangeSet) it.next();
			script.append(markAsApplied(changeSet));
		}
		return script;
	}
	
	private Script getMigrationScript() {
		Script script = new Script();
		Iterator it = changeSets.iterator();
		while (it.hasNext()) {
			ChangeSet changeSet = (ChangeSet) it.next();
			if (!isApplied(changeSet)) {
				script.append(changeSet.getScript(dialect));
				script.append(markAsApplied(changeSet));
			}
		}
		return script;
	}
	
	/**
	 * Returns whether the module is new or whether any entries previously 
	 * existed in the log-table.
	 */
	public boolean isNewModule() {
		return newModule;
	}
	
	/**
	 * Returns whether the given changeSet has already been applied.
	 * @throws DatabaseOutOfSyncException If the ChangeSet id in the log-table
	 * 		   doesn't match the the one of the ChangeSet
	 */
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
	
	/**
	 * Returns a script that can be used to add an entry to the log-table that
	 * marks the given ChangeSet as applied. 
	 * @throws DatabaseOutOfSyncException If the sequence number of the 
	 * 		   ChangeSet doesn't match the number of already applied changes.
	 */
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
