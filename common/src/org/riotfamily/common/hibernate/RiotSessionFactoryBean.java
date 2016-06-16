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
package org.riotfamily.common.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.jdbc.Work;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.riotfamily.common.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.util.StringUtils;

/**
 * SessionFactory that validates the database schema by checking if
 * {@link Configuration#generateSchemaUpdateScript(Dialect, DatabaseMetadata)}
 * returns any statements. If so, a HibernateException is thrown and the 
 * update script is logged.
 */
public class RiotSessionFactoryBean extends LocalSessionFactoryBean {

	private Logger log = LoggerFactory.getLogger(RiotSessionFactoryBean.class);
	
	private boolean validate = false;
	
	private Interceptor interceptor;
	
	private List<FilterDefinition> filterDefinitions;

	/**
	 * Default is <code>false</code> because of:
	 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-3532
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	
	@Override
	public void setEntityInterceptor(Interceptor interceptor) {
		super.setEntityInterceptor(interceptor);
		this.interceptor = interceptor;
	}
	
	public void setFilterDefinitions(List<FilterDefinition> filterDefinitions) {
		this.filterDefinitions = filterDefinitions;
	}
	
	@Override
	protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
		
		if (filterDefinitions != null && filterDefinitions.size() > 0) {
			for (FilterDefinition filterDefinition : filterDefinitions) {
				getConfiguration().addFilterDefinition(filterDefinition);
			}
		}
 		
		SessionFactory sessionFactory = super.buildSessionFactory(sfb);
		
		if (validate) {
			validateSchema(sessionFactory);
		}
		if (interceptor instanceof SessionFactoryAwareInterceptor) {
			((SessionFactoryAwareInterceptor) interceptor).setSessionFactory(sessionFactory);
		}
		
		return sessionFactory;
	}
	
	public void validateSchema(SessionFactory sessionFactory)
			throws DataAccessException {
		Session session = sessionFactory.openSession(); 
		try {
			session.doWork(new Work() {
				public void execute(Connection connection) throws SQLException {
					Dialect dialect = Dialect.getDialect(getConfiguration()
							.getProperties());
					DatabaseMetadata metadata = new DatabaseMetadata(connection,
							dialect, getConfiguration());
					List<SchemaUpdateScript> schemaUpdates = getConfiguration()
							.generateSchemaUpdateScriptList(dialect, metadata);
					if (schemaUpdates.size() > 0) {
						log.error("The database schema is not up-to-date.\n"
								+ "Please execute the following statements:\n"
								+ FormatUtils.repeat("-", 80) + "\n"
								+ StringUtils.arrayToDelimitedString(
										SchemaUpdateScript
												.toStringArray(schemaUpdates),
										";\n") + ";\n"
								+ FormatUtils.repeat("-", 80));
	
						throw new HibernateException("Invalid database schema");
					}
				}
			});
		} 
		finally {
			session.close();
		}
	}

}
