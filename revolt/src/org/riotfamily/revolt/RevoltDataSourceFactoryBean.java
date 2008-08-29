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
package org.riotfamily.revolt;

import java.util.Collection;

import javax.sql.DataSource;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.DialectResolver;
import org.riotfamily.revolt.support.LogTable;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

public class RevoltDataSourceFactoryBean implements FactoryBean, 
		ApplicationContextAware {

	private RiotLog log = RiotLog.get(RevoltDataSourceFactoryBean.class);
	
	private DataSource dataSource;

	private boolean automatic;
	
	private LogTable logTable;
	
	private Dialect dialect;
	
	private Script script = new Script();
	
	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.dialect = new DialectResolver().getDialect(dataSource);
		this.logTable = new LogTable(dataSource, dialect);
	}
	
	/**
	 * Sets whether Revolt should automatically apply pending refactorings.
	 */
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
			
	private String getInstructions() {
		String sql = script.getSql();
		if (StringUtils.hasLength(sql)) {
			StringBuffer sb = new StringBuffer();
			sb.append("\n\n-------------------------------------------------------------------------\n\n");
			sb.append("The database ").append(DatabaseUtils.getUrl(dataSource));
			sb.append(" is not up-to-date.\n" 
					+ "Please execute the following SQL commands (or turn on automatic migration)\n"
					+ "in order to evolve the schema:\n\n");
					
			sb.append(sql);
			sb.append("\n\n-------------------------------------------------------------------------\n\n");
			return sb.toString();
		}
		return null;
	}
		
	//---------------------------------------------------------------------
	// Implementation of ApplicationContextAware interface
	//---------------------------------------------------------------------
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		Collection<EvolutionHistory> evolutions = 
			SpringUtils.listBeansOfTypeIncludingAncestors(
			applicationContext, EvolutionHistory.class);
		
		if (!evolutions.isEmpty()) {
			if (!logTable.exists()) {
				log.info("Revolt log-table does not exist.");
				script.append(logTable.getCreateTableScript());
			}
			for (EvolutionHistory history : new EvolutionHistoryList(evolutions)) {
				history.init(logTable);
				script.append(history.getScript(dialect));
			}
			
			if (automatic && !script.isManualExecutionOnly()) {
				new TransactionTemplate(new DataSourceTransactionManager(dataSource)).execute(
					new TransactionCallbackWithoutResult() {
						protected void doInTransactionWithoutResult(TransactionStatus status) {
							script.execute(dataSource);
						}
					}
				);
			}
			else {
				String instructions = getInstructions();
				if (instructions != null) {
					log.fatal(instructions);
					throw new FatalBeanException("Database not up-to-date. " 
							+ "See instructions above.");
				}
			}
		}
	}
	
	//---------------------------------------------------------------------
	// Implementation of FactoryBean interface
	//---------------------------------------------------------------------
	
	public Object getObject() throws Exception {
		return dataSource;
	}

	public Class<?> getObjectType() {
		return DataSource.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	
}
