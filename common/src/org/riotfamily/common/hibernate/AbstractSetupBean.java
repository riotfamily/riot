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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class AbstractSetupBean implements ApplicationContextAware, InitializingBean {

	private PlatformTransactionManager tx;
	
	private TransactionDefinition txdef = new DefaultTransactionDefinition();

	private SessionFactory sessionFactory;

	private ApplicationContext applicationContext;
	
	public AbstractSetupBean(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setTransactionManager(PlatformTransactionManager tx) {
		this.tx = tx;
	}
	
	public void setTxdef(TransactionDefinition txdef) {
		this.txdef = txdef;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;
	}
	
	public final void afterPropertiesSet() {
		if (tx == null) {
			tx = SpringUtils.beanOfTypeIncludingAncestors(
					applicationContext, PlatformTransactionManager.class);
		}
		try {
			performSetup();
		}
		catch (Exception e) {
			throw new FatalBeanException("Exception during setup", e);
		}
	}
	
	private void performSetup() throws Exception {
		new ThreadBoundHibernateTemplate(sessionFactory).execute(new HibernateCallbackWithoutResult() {
			@Override
			public void doWithoutResult(Session session) throws Exception {
				TransactionStatus status = tx.getTransaction(txdef);
				try {
					setup(session);
				}
				catch (Exception e) {
					tx.rollback(status);
					throw e;
				}
				tx.commit(status);
			}
		});
	}
	
	protected abstract void setup(Session session) throws Exception;
	
}
