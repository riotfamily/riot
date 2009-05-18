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
package org.riotfamily.common.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.RiotLog;
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

	private RiotLog log = RiotLog.get(RiotSessionFactoryBean.class);
	
	private boolean validate = false;
	
	@Override
	protected void afterSessionFactoryCreation() throws Exception {
		super.afterSessionFactoryCreation();
		if (validate) {
			validateSchema();
		}
	}
	
	/**
	 * Default is <code>false</code> because of:
	 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-3532
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
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
