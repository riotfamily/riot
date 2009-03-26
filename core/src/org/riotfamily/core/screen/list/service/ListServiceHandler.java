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
package org.riotfamily.core.screen.list.service;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.ListScreen;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class ListServiceHandler {

	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);
	
	protected ListService service;
	
	protected ListState state;
	
	protected ListScreen screen;
	
	protected RiotDao dao;
	
	protected HttpServletRequest request;
	
	protected MessageResolver messageResolver;
	
	protected ScreenContext screenContext;
	
	private PlatformTransactionManager transactionManager;
	
	public ListServiceHandler(ListService service, String key, 
			HttpServletRequest request) {
		
		this.service = service;
		this.state = ListState.get(request, key);
		this.screen = service.getScreen(state);
		this.dao = screen.getDao();
		this.request = request;
		this.messageResolver = service.getMessageResolver(request);
		this.transactionManager = service.getTransactionManager();
		this.screenContext = new ScreenContext(screen, request, null, 
				state.getParentId(), false);
	}
	
	protected ListServiceHandler(ListServiceHandler other) {
		this.service = other.service;
		this.state = other.state;
		this.screen = other.screen;
		this.dao = other.dao;
		this.request = other.request;
		this.messageResolver = other.messageResolver;
		this.transactionManager = other.transactionManager;
		this.screenContext = other.screenContext;
	}
		
	protected TransactionStatus beginTransaction() {
		return transactionManager.getTransaction(TRANSACTION_DEFINITION);
	}
	
	protected void commit(TransactionStatus ts) {
		transactionManager.commit(ts);
	}
	
	protected void rollback(TransactionStatus ts) {
		transactionManager.rollback(ts);
	}

}
