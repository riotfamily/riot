package org.riotfamily.common.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.DeferredRenderingResponseWrapper;
import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * HandlerInterceptor that allows an included controller to handle the request
 * before any other controller is executed. This is useful if an included 
 * controller needs to send a redirect, which would not work under normal 
 * circumstances, since the response would have already been committed.
 * <p>
 * The interceptor looks for a special request parameter which contains the URI
 * of the controller that should handle the request in the fist place. If such
 * a parameter is present, the interceptor will use a RequestDispatcher to 
 * forward the request to that URI. For this forward the response is replaced
 * by a wrapper that captures the output and checks whether a redirect or error 
 * has been sent. If yes, the interceptor prevents the execution of the actual 
 * handler. Otherwise the execution continues and the captured output is 
 * rendered when the controller is requested for the second time.
 * </p> 
 */
public class PushUpInterceptor extends HandlerInterceptorAdapter {

	public static final String INCLUDE_URI_PARAM = "__includeUri";
	
	private static final String DISPATCHED_ATTRIBUTE  = 
			PushUpInterceptor.class.getName() + ".dispatched";
	
	private static final String HANDLER_ATTRIBUTE = 
			PushUpInterceptor.class.getName() + ".handler";
	
	private static final String RESPONSE_WRAPPER_ATTRIBUTE = 
		PushUpInterceptor.class.getName() + ".responseWrapper";

	
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		Object firstHandler = request.getAttribute(HANDLER_ATTRIBUTE);
		if (firstHandler == null) {
			boolean dispatched = request.getAttribute(DISPATCHED_ATTRIBUTE) != null;
			if (dispatched) {
				return handleFirstInclude(handler, request);
			}
			return handleUnknown(request, response);
		}
		return handleSubsequentInclude(handler, firstHandler, request, response);
	}
		
	protected boolean handleUnknown(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		String includeUri = request.getParameter(INCLUDE_URI_PARAM);
		if (includeUri != null) {
			request.setAttribute(DISPATCHED_ATTRIBUTE, Boolean.TRUE);
			if (ServletUtils.isXmlHttpRequest(request)) {
				request.getRequestDispatcher(includeUri).forward(
						request, response);
				
				return false;
			}
			else {
				DeferredRenderingResponseWrapper responseWrapper = 
						new DeferredRenderingResponseWrapper(response);
				
				request.setAttribute(RESPONSE_WRAPPER_ATTRIBUTE, responseWrapper);			
				request.getRequestDispatcher(includeUri).forward(
						request, responseWrapper);
				
				if (responseWrapper.isRedirectSent()) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected boolean handleFirstInclude(Object handler, HttpServletRequest request) {
		request.setAttribute(HANDLER_ATTRIBUTE, handler);
		return true;
	}
	
	protected boolean handleSubsequentInclude(Object handler, 
			Object firstHandler, HttpServletRequest request, 
			HttpServletResponse response) throws IOException {

		//TODO Check if getRequestURI() matches the includeUri, to support
		//multiple includes of the same handler under different URLs.
		if (handler.equals(firstHandler)) {
			//Second time we come across the handler ...
			DeferredRenderingResponseWrapper responseWrapper = 
				(DeferredRenderingResponseWrapper) request.getAttribute(
				RESPONSE_WRAPPER_ATTRIBUTE);
			
			responseWrapper.renderResponse(response);
			return false;
		}
		return true;
	}
}
