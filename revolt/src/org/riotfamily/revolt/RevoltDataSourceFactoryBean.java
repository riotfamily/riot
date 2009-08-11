package org.riotfamily.revolt;

import java.util.Collection;

import javax.sql.DataSource;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.revolt.support.DatabaseUtils;
import org.riotfamily.revolt.support.DialectResolver;
import org.riotfamily.revolt.support.LogTable;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

public class RevoltDataSourceFactoryBean implements FactoryBean, 
		ApplicationContextAware, InitializingBean {

	private RiotLog log = RiotLog.get(RevoltDataSourceFactoryBean.class);
	
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
		evolutions = SpringUtils.listBeansOfTypeIncludingAncestors(
			applicationContext, EvolutionHistory.class);
	}
	
	//---------------------------------------------------------------------
	// Implementation of InitializingBean interface
	//---------------------------------------------------------------------
	
	public void afterPropertiesSet() throws Exception {
		if (!evolutions.isEmpty()) {
			Dialect dialect = new DialectResolver().getDialect(dataSource);
			SimpleJdbcTemplate template = new SimpleJdbcTemplate(dataSource);
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
