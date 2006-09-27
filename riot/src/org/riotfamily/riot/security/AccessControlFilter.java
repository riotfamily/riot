package org.riotfamily.riot.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter that binds the authenticated principal (if present) to the
 * current thread. 
 * 
 * @see AccessController
 */
public final class AccessControlFilter extends OncePerRequestFilter {

	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) 
			throws ServletException, IOException {
		
		try {
			AccessController.bindPrincipalToCurrentTread(request);
			filterChain.doFilter(request, response);
		}
		finally {
			AccessController.resetPrincipal();
		}
	}

}
