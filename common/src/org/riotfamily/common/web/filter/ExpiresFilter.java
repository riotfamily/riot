package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Servlet filter that sets an <code>Expires</code> header for each request.
 */
public class ExpiresFilter extends GenericFilterBean {

	private static final String HEADER_EXPIRES = "Expires";
	private long expiresAfter;
	
	public void setExpiresAfter(String expires) {
		this.expiresAfter = FormatUtils.parseMillis(expires);
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain filterChain) throws IOException, ServletException {
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.addDateHeader(HEADER_EXPIRES, 
				System.currentTimeMillis() + expiresAfter);
		
		filterChain.doFilter(request, response);
	}

}
