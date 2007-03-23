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
package org.riotfamily.common.web.transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.LastModified;

/**
 * HandlerAdapter that handles the request within a transaction. The transaction
 * is rolled back when an exception is thrown by the controller.
 * <p>
 * NOTE: HandlerAdapters can't be chained. If your use the 
 * {@link org.riotfamily.cachius.spring.CacheableControllerHandlerAdapter} and a 
 * controller implements both {@link org.riotfamily.cachius.spring.CacheableController} 
 * and {@link TransactionalController} the adapter with the highest precedence 
 * will be used to handle the request. So if you want to cache a 
 * {@link TransactionalController} use an AOP proxy instead of this class.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class TransactionalControllerHandlerAdapter 
		implements HandlerAdapter, Ordered {

	private PlatformTransactionManager transactionManager;
	
	private int order = Integer.MAX_VALUE - 1;
	
	public TransactionalControllerHandlerAdapter(
			PlatformTransactionManager transactionManager) {
		
		this.transactionManager = transactionManager;
	}
	
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean supports(Object handler) {
		return handler instanceof TransactionalController;
	}
	
	public ModelAndView handle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) 
			throws Exception {
		
		Controller controller = (Controller) handler;
		
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		TransactionStatus status = transactionManager.getTransaction(def);
		ModelAndView mv;
		try {
			mv = controller.handleRequest(request, response);
		}
		catch (Exception ex) {
			transactionManager.rollback(status);
		    throw ex;
		}
		transactionManager.commit(status);
		return mv;
	}
	
	public long getLastModified(HttpServletRequest request, Object handler) {
		if (handler instanceof LastModified) {
			return ((LastModified) handler).getLastModified(request);
		}
		return -1L;
	}
	
}
