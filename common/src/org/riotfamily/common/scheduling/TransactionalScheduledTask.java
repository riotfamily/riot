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
package org.riotfamily.common.scheduling;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class TransactionalScheduledTask extends ScheduledTaskSupport {

	private PlatformTransactionManager tx;
	
	private TransactionDefinition txdef = new DefaultTransactionDefinition();
	
	public TransactionalScheduledTask(PlatformTransactionManager tx) {
		this.tx = tx;
	}
	
	public void setTxdef(TransactionDefinition txdef) {
		this.txdef = txdef;
	}
	
	public final void execute() throws Exception {
		TransactionStatus status = tx.getTransaction(txdef);
		try {
			executeInTransaction();
		}
		catch (Exception e) {
			tx.rollback(status);
			throw e;
		}
		tx.commit(status);
	}
	
	protected abstract void executeInTransaction() throws Exception;

}
