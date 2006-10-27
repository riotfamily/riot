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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.search.crawler.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.riotfamily.search.crawler.LinkFilter;


public class RegExLinkFilter implements LinkFilter {	
	
	private List includes;
	
	private List excludes;
	
	public void setIncludes(String[] patterns) {
		includes = new ArrayList(patterns.length);
		for (int i = 0; i < patterns.length; i++) {
			includes.add(Pattern.compile(patterns[i]));
		}	
	}
	
	public void setExcludes(String[] patterns) {
		excludes = new ArrayList(patterns.length);
		for (int i = 0; i < patterns.length; i++) {
			excludes.add(Pattern.compile(patterns[i]));
		}	
	}
	
	public boolean accept(String baseUrl, String link) {
		if (anyMatch(includes, link)) {
			return !anyMatch(excludes, link);
		}
		return false;
	}
	
	private boolean anyMatch(Collection patterns, String link) {
		if (patterns != null) {
			Iterator it = patterns.iterator();
			while (it.hasNext()) {
				Pattern pattern = (Pattern) it.next();
				if (pattern.matcher(link).matches()) {
					return true;			
				}
			}
		}
		return false;
	}
	
}
