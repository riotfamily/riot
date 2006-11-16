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
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * 
 */
public class EvolutionHistory implements InitializingBean {

	private List changeSets;

	private DataSource dataSource;

	private Dialect dialect;

	private LogTable logTable;
	
	public EvolutionHistory(String moduleName, DataSource dataSource, 
			DialectResolver dialectResolver) {
		
		this.dataSource = dataSource;
		dialect = dialectResolver.getDialect(dataSource);
		logTable = new LogTable(dataSource, dialect, moduleName);
	}

	public void setChangeSets(ChangeSet[] changeSets) {
		this.changeSets = new ArrayList();
		for (int i = 0; i < changeSets.length; i++) {
			ChangeSet changeSet = changeSets[i];
			changeSet.setSequenceNumber(i);
			this.changeSets.add(changeSet);
		}
	}

	private void markAllChangesAsApplied() {
		Iterator it = changeSets.iterator();
		while (it.hasNext()) {
			ChangeSet changeSet = (ChangeSet) it.next();
			changeSet.markAsApplied(logTable);
		}
	}
	
	private void evolveDatabase() {
		Database model = new Database();
		Iterator it = changeSets.iterator();
		while (it.hasNext()) {
			ChangeSet changeSet = (ChangeSet) it.next();
			changeSet.applyIfNeeded(dataSource, dialect, logTable, model);
		}
		if (!DatabaseUtils.databaseMatchesModel(dataSource, model)) {
			throw new DatabaseOutOfSyncException();
		}
	}
	
	private Database evolveModel() {
		Database model = new Database();
		Iterator it = changeSets.iterator();
		while (it.hasNext()) {
			ChangeSet changeSet = (ChangeSet) it.next();
			changeSet.applyToModel(model);
		}
		return model;
	}
	
	public void evolve() throws DatabaseOutOfSyncException {
		if (!logTable.hasEntries()) {
			if (DatabaseUtils.databaseMatchesModel(dataSource, evolveModel())) {
				markAllChangesAsApplied();
				return;
			}
		}
		evolveDatabase();
	}
	
	public void afterPropertiesSet() {
		evolve();
	}

}
