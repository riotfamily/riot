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
package org.riotfamily.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.springframework.beans.support.PropertyComparator;

public final class PackageLister {

	private static final Comparator PACKAGE_COMPARATOR = 
			new PropertyComparator("implementationTitle", true, true);
	
	private PackageLister() {
	}

	public static Collection<Package> listPackages(String[] patterns) {
		HashMap<String, Package> map = new HashMap<String, Package>();
		Package[] packages = Package.getPackages();
	    for (int i = 0; i < packages.length; i++) {
	    	String name = packages[i].getImplementationTitle(); 
	        if (name != null && matches(packages[i].getName(), patterns)) {
	        	map.put(name, packages[i]);
	        }
	    }
	    ArrayList<Package> result = new ArrayList<Package>(map.values());
	    Collections.sort(result, PACKAGE_COMPARATOR);
	    return result;
	}
	
	private static boolean matches(String name, String[] patterns) {
		if (patterns == null) {
			return true;
		}
		for (int i = 0; i < patterns.length; i++) {
			String pattern = patterns[i];
			if (pattern.indexOf('*') != -1) {
				pattern = pattern.substring(0, pattern.indexOf('*'));
				if (name.startsWith(pattern)) {
					return true;
				}
			}
			else {
				if (name.equals(pattern)) {
					return true;
				}
			}
		}
		return false;
	}
}
