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
