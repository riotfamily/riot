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
package org.riotfamily.core.screen.list.service;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.DefaultScreenContext;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.screen.list.ListState;
import org.riotfamily.core.screen.list.TreeListScreen;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Abstract base class for the different service handlers. Provides access to
 * all relevant context objects via protected fields and offers methods for
 * transaction handling.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
abstract class ListServiceHandler {

	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);
	
	protected ListService service;
	
	protected ListState state;
	
	protected TreeListScreen screen;
	
	protected RiotDao dao;
	
	protected HttpServletRequest request;
	
	protected ScreenContext screenContext;
	
	protected ListScreen chooserTarget;
	
	private PlatformTransactionManager transactionManager;
	
	ListServiceHandler(ListService service, String key, 
			HttpServletRequest request) {
		
		this.service = service;
		this.state = ListState.get(request, key);
		this.screen = service.getScreenRepository().getScreen(
				state.getScreenId(), TreeListScreen.class);
		
		this.dao = screen.getDao();
		this.request = request;
		this.transactionManager = service.getTransactionManager();
		this.screenContext = new DefaultScreenContext(screen, request, 
				null, state.getParentId(), false);
		
		if (state.getChooserSettings().getTargetScreenId() != null) {
			String id = state.getChooserSettings().getTargetScreenId();
			chooserTarget = ScreenUtils.getListScreen(
					service.getScreenRepository().getScreen(
					id, RiotScreen.class));
		}
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
