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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Acts like a {@link FilterChain}, just for FilterPlugins.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class PluginChain implements FilterChain {

	private FilterChain filterChain;
	
	private FilterPlugin[] plugins;
	
	private int nextPlugin = 0;
	
	public PluginChain(FilterChain filterChain, FilterPlugin[] plugins) {
		this.filterChain = filterChain;
		this.plugins = plugins;
	}

	public void doFilter(HttpServletRequest request, 
			HttpServletResponse response) 
			throws IOException, ServletException {
		
		if (nextPlugin < plugins.length) {
			plugins[nextPlugin++].doFilter(request, response, this);
		}
		else {
			filterChain.doFilter(request, response);
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		
		doFilter((HttpServletRequest) request, (HttpServletResponse) response);
	}
}
