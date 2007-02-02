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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.LogTable;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 */
public class Evolver implements ApplicationContextAware {

	private boolean automatic;
	
	private boolean validate = true;
	
	private HashMap scripts = new HashMap();
	
	private HashMap logTables = new HashMap();
	
	/**
	 * Sets whether Revolt should automatically apply pending refactorings.
	 */
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
	
	/**
	 * Sets whether Revolt should validate the schema. 
	 * Default is <code>true</code>.
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		Collection evolutions = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, EvolutionHistory.class).values();
		
		if (automatic || validate) {
			Iterator it = evolutions.iterator();
			while (it.hasNext()) {
				EvolutionHistory history = (EvolutionHistory) it.next();
				history.init(getLogTable(history));
				if (automatic) {
					history.evolve();
				}
				else {
					getScript(history).append(history.getScript());
				}
			}
			if (!automatic) {
				String instructions = getInstructions();
				if (StringUtils.hasLength(instructions)) {
					throw new EvolutionInstructions(instructions);
				}
			}
		}
	}
	
	private LogTable getLogTable(EvolutionHistory history) {
		DataSource dataSource = history.getDataSource();
		LogTable logTable = (LogTable) logTables.get(dataSource);
		if (logTable == null) {
			logTable = new LogTable(dataSource, history.getDialect());
			logTables.put(history.getDataSource(), logTable);
			if (!logTable.exists()) {
				if (automatic) {
					logTable.create();
				}
				else {
					getScript(history).append(logTable.getCreateTableScript());
				}
			}
		}
		return logTable;
	}
	
	private Script getScript(EvolutionHistory history) {
		DataSource dataSource = history.getDataSource();
		Script script = (Script) scripts.get(dataSource);
		if (script == null) {
			script = new Script();
			scripts.put(dataSource, script);
		}
		return script;
	}
		
	private String getInstructions() {
		StringBuffer sb = new StringBuffer();
		Iterator it = scripts.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			DataSource dataSource = (DataSource) entry.getKey();
			Script script = (Script) entry.getValue();
			String sql = script.getSql();
			if (StringUtils.hasLength(sql)) {
				sb.append("\n\n-------------------------------------------------------------------------\n\n");
				sb.append("The database ").append(DatabaseUtils.getUrl(dataSource));
				sb.append(" is not up-to-date.\nPlease execute the" 
						+ " following SQL commands to evolve the schema:\n\n");
						
				sb.append(sql);
				sb.append("\n\n-------------------------------------------------------------------------\n\n");
			}
		}
		return sb.toString();
	}
	
}
