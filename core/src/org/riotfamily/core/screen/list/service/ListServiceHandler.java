package org.riotfamily.core.screen.list.service;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.core.dao.RiotDao;
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
	
	protected MessageResolver messageResolver;
	
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
		this.messageResolver = service.getMessageResolver(request);
		this.transactionManager = service.getTransactionManager();
		this.screenContext = new ScreenContext(screen, request, 
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
