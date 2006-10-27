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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
