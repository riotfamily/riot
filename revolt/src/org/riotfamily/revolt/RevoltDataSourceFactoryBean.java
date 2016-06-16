/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.revolt;

import java.util.Collection;

import javax.sql.DataSource;

import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.DialectResolver;
import org.riotfamily.revolt.support.LogTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

public class RevoltDataSourceFactoryBean implements FactoryBean<DataSource>, 
		ApplicationContextAware, InitializingBean {

	private Logger log = LoggerFactory.getLogger(RevoltDataSourceFactoryBean.class);
	
	private DataSource dataSource;

	private boolean automatic;
	
	private Script script = new Script();

	private Collection<EvolutionHistory> evolutions;
	
	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
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
		evolutions = BeanFactoryUtils.beansOfTypeIncludingAncestors(
				applicationContext, EvolutionHistory.class).values();
	}
	
	//---------------------------------------------------------------------
	// Implementation of InitializingBean interface
	//---------------------------------------------------------------------
	
	public void afterPropertiesSet() throws Exception {
		if (!evolutions.isEmpty()) {
			Dialect dialect = new DialectResolver().getDialect(dataSource);
			JdbcTemplate template = new JdbcTemplate(dataSource);
			LogTable logTable = new LogTable(template, dialect);
			if (!logTable.exists()) {
				log.info("Revolt log-table does not exist.");
				script.append(logTable.getCreateTableScript());
			}

			for (EvolutionHistory history : new EvolutionHistoryList(evolutions)) {
				history.init(logTable, template);
				script.append(history.getScript(dialect, template));
			}
			
			if (automatic && !script.isManualExecutionOnly()) {
				new TransactionTemplate(new DataSourceTransactionManager(dataSource)).execute(
					new TransactionCallbackWithoutResult() {
						@Override
						protected void doInTransactionWithoutResult(TransactionStatus status) {
							script.execute(dataSource);
						}
					}
				);
			}
			else {
				String instructions = getInstructions();
				if (instructions != null) {
					log.error(instructions);
					throw new FatalBeanException("Database not up-to-date. " 
							+ "See instructions above.");
				}
			}
		}
	}
	
	//---------------------------------------------------------------------
	// Implementation of FactoryBean interface
	//---------------------------------------------------------------------
	
	public DataSource getObject() throws Exception {
		return dataSource;
	}

	public Class<DataSource> getObjectType() {
		return DataSource.class;
	}

	public boolean isSingleton() {
		return true;
	}
	
	
}
