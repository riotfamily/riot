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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * HandlerInterceptor that executes the handler within a transactional context. 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class TransactionalHandlerInterceptor implements HandlerInterceptor {

	private static final String TX_STATUS_ATTRIBUTE = 
			TransactionalHandlerInterceptor.class.getName() + ".status";
	
	private static Log log = LogFactory.getLog(TransactionalHandlerInterceptor.class);
	
	private TransactionAttribute transactionAttribute = 
			new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
	
	private PlatformTransactionManager transactionManager;
	
	public TransactionalHandlerInterceptor(PlatformTransactionManager tm) {
		this.transactionManager = tm;
	}
		
	/**
	 * Sets the {@link TransactionAttribute}. If not set, a 
	 * {@link DefaultTransactionAttribute} with 
	 * {@link TransactionDefinition#PROPAGATION_REQUIRED} is used.
	 */
	public void setTransactionAttribute(TransactionAttribute transactionAttribute) {
		this.transactionAttribute = transactionAttribute;
	}
	
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		if (handler instanceof TransactionalHandler) {
			log.debug("Beginning transaction");
			TransactionStatus status = transactionManager.getTransaction(
					transactionAttribute);
			
			request.setAttribute(TX_STATUS_ATTRIBUTE, status);
		}
		return true;
	}
	
	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		if (handler instanceof TransactionalHandler) {
			TransactionStatus status = (TransactionStatus) 
					request.getAttribute(TX_STATUS_ATTRIBUTE);
			
			log.debug("Committing transaction");
			transactionManager.commit(status);
		}
	}
	
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		if (handler instanceof TransactionalHandler) {
			if (ex != null) {
				TransactionStatus status = (TransactionStatus) 
						request.getAttribute(TX_STATUS_ATTRIBUTE);
				
				log.debug("Rolling back transaction");
				transactionManager.rollback(status);
			}
			request.removeAttribute(TX_STATUS_ATTRIBUTE);
		}
	}
}
