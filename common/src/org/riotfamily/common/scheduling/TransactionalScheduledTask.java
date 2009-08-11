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
