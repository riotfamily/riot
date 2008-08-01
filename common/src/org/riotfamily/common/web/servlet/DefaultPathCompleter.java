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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.servlet;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class DefaultPathCompleter implements PathCompleter {

	private String servletPrefix;

	private String servletSuffix;
	
	
	public DefaultPathCompleter() {
	}
	
	public DefaultPathCompleter(String servletPrefix, String servletSuffix) {
		this.servletPrefix = servletPrefix;
		this.servletSuffix = servletSuffix;
	}
	
	protected void setServletPrefix(String servletPrefix) {
		this.servletPrefix = servletPrefix;
	}

	protected void setServletSuffix(String servletSuffix) {
		this.servletSuffix = servletSuffix;
	}

	public String addServletMapping(String path) {
		if (servletSuffix != null) {
			int i = path.indexOf('?');
			if (i != -1) {
				return path.substring(0, i) + servletSuffix + path.substring(i);
			}
			else {
				return path + servletSuffix;
			}
		}
		else if (servletPrefix != null) {
			return servletPrefix + path;
		}
		return path;
	}
	
	public String stripServletMapping(String path) {
		if (servletPrefix != null) {
			if (path.startsWith(servletPrefix)) {
				return path.substring(servletPrefix.length());
			}
		}
		else if (path.endsWith(servletSuffix)) {
			return path.substring(0, path.length() - servletSuffix.length());
		}
		return path;
	}
}
