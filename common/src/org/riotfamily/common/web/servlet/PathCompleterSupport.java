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
public class PathCompleterSupport implements PathCompleter {

	private String prefix;

	private String suffix;
	
	
	public PathCompleterSupport() {
	}
	
	public PathCompleterSupport(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	protected void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String addMapping(String path) {
		if (suffix != null) {
			int i = path.indexOf('?');
			if (i != -1) {
				return path.substring(0, i) + suffix + path.substring(i);
			}
			else {
				return path + suffix;
			}
		}
		else if (prefix != null) {
			return prefix + path;
		}
		return path;
	}
	
	public String stripMapping(String path) {
		if (prefix != null) {
			if (path.startsWith(prefix)) {
				return path.substring(prefix.length());
			}
		}
		else if (path.endsWith(suffix)) {
			return path.substring(0, path.length() - suffix.length());
		}
		return path;
	}
	
	public boolean containsMapping(String path) {
		return (path != null 
				&& (prefix == null || path.startsWith(prefix)) 
				&& (suffix == null || path.endsWith(suffix)))
				|| (prefix == null && suffix == null);
	}
}
