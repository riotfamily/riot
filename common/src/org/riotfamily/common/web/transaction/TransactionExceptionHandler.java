package org.riotfamily.common.web.transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.TransactionException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interface that can be implemented by 
 * {@link TransactionalHandler}s in order to react on errors during a commit 
 * or rollback operation.
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface TransactionExceptionHandler extends TransactionalHandler {

	/**
	 * Invoked by the {@link TransactionalHandlerInterceptor} when a 
	 * {@link TransactionException} occurs during commit. Implementors may
	 * return a ModelAndView or <code>null</code> in which case the exception
	 * is re-thrown.
	 * 
	 * @param ex The exception thrown by the PlatformTransactionManager
	 * @param modelAndView The original ModelAndView
	 * @param request The request
	 * @param response The response
	 * @return A ModelAndView or <code>null</code>
	 */
	public ModelAndView commitFailed(TransactionException ex,
			ModelAndView modelAndView, HttpServletRequest request, 
			HttpServletResponse response);
	
	/**
	 * Invoked by the {@link TransactionalHandlerInterceptor} when a 
	 * {@link TransactionException} occurs during rollback. Implementors may
	 * return a ModelAndView or <code>null</code> in which case the exception
	 * is re-thrown.
	 * 
	 * @param ex The exception thrown by the PlatformTransactionManager
	 * @param rollbackCause The exception that caused the rollback
	 * @param request The request
	 * @param response The response
	 * @return A ModelAndView or <code>null</code>
	 */
	public ModelAndView rollbackFailed(TransactionException ex,
			Exception rollbackCause, HttpServletRequest request, 
			HttpServletResponse response);

}
