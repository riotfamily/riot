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
package org.riotfamily.website.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter that forwards all request to a named dispatcher.
 * Example: 
 * <pre> 
 * &lt;filter>
 *   &lt;filter-name>root-path-filter&lt;/filter-name>
 *   &lt;filter-class>org.riotfamily.website.filter.ForwardToServletFilter&lt;/filter-class>
 *   &lt;init-param>
 *     &lt;param-name>servletName&lt;/param-name>
 *     &lt;param-value>website&lt;/param-value>
 *  &lt;/init-param>
 * &lt;/filter>
 * 
 * &lt;filter-mapping>
 *   &lt;filter-name>root-path-filter&lt;/filter-name>
 *   &lt;url-pattern>/&lt;/url-pattern>
 * &lt;/filter-mapping>
 * </pre>
 * <p>
 * This will forward all request to / to the website servlet.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ForwardToServletFilter extends GenericFilterBean {

	private String servletName;
	
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	public void doFilter(ServletRequest request, 
			ServletResponse response, FilterChain filterChain) 
			throws IOException, ServletException {

		RequestDispatcher rd = getServletContext().getNamedDispatcher(servletName);
		rd.forward(request, response);
	}
	
}
