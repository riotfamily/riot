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
package org.riotfamily.riot;

/**
 * Class that exposes the Riot version. Fetches the Implementation-Version 
 * attribute from the manifest contained in the jar file.
 */
public final class RiotVersion {

	private static String versionString;
	
	private RiotVersion() {
	}
	
	/**
     * Return the full Riot version string.
     * @see java.lang.Package#getImplementationVersion
     */
    public static String getVersionString() {
    	if (versionString == null) {
    		versionString = RiotVersion.class.getPackage().getImplementationVersion();
    	}
    	return versionString;
    }
    
}
