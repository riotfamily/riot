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

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.riotfamily.common.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.util.StringUtils;

/**
 * SessionFactory that validates the database schema by checking if
 * {@link Configuration#generateSchemaUpdateScript(Dialect, DatabaseMetadata)}
 * returns any statements. If so, a HibernateException is thrown and the 
 * update script is logged.
 */
public class RiotSessionFactoryBean extends AnnotationSessionFactoryBean {

	private Logger log = LoggerFactory.getLogger(RiotSessionFactoryBean.class);
	
	private boolean validate = false;
	
	private Interceptor interceptor;

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
	
	@Override
	protected void afterSessionFactoryCreation() throws Exception {
		super.afterSessionFactoryCreation();
		if (validate) {
			validateSchema();
		}
		if (interceptor instanceof SessionFactoryAwareInterceptor) {
			((SessionFactoryAwareInterceptor) interceptor).setSessionFactory(getSessionFactory());
		}
	}
	
	public void validateSchema() throws DataAccessException {
		HibernateTemplate hibernateTemplate = new HibernateTemplate(getSessionFactory());
		hibernateTemplate.setFlushMode(HibernateTemplate.FLUSH_NEVER);
		hibernateTemplate.execute(
			new HibernateCallback() {
				@SuppressWarnings("deprecation")
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Connection con = session.connection();
					Dialect dialect = Dialect.getDialect(getConfiguration().getProperties());
					DatabaseMetadata metadata = new DatabaseMetadata(con, dialect);
					String[] sql = getConfiguration().generateSchemaUpdateScript(dialect, metadata);
					if (sql.length > 0) {
						log.error("The database schema is not up-to-date.\n"
								+ "Please execute the following statements:\n"
								+ FormatUtils.repeat("-", 80) + "\n"
								+ StringUtils.arrayToDelimitedString(sql, ";\n")
								+ ";\n" + FormatUtils.repeat("-", 80));
						
						throw new HibernateException("Invalid database schema");
					}
					return null;
				}
			}
		);
	}

}
