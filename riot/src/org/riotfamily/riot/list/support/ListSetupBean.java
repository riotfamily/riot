package org.riotfamily.riot.list.support;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.riot.dao.RiotDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class ListSetupBean implements InitializingBean {

	private RiotDao dao;
	
	private List items;

	private PlatformTransactionManager transactionManager;
	
	public void setItems(List items) {
		this.items = items;
	}

	public void setDao(RiotDao listModel) {
		this.dao = listModel;
	}
	
	public void setTransactionManager(PlatformTransactionManager tm) {
		this.transactionManager = tm;
	}

	public void afterPropertiesSet() throws Exception {
		ListParamsImpl params = new ListParamsImpl();
		Collection c = dao.list(null, params);
		if (c.isEmpty()) {
			new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus ts) {
					saveItems();
				}
			});
		}
	}	
	
	protected void saveItems() {
		Iterator it = items.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			dao.save(item, null);
		}
	}
	
}
