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
package org.riotfamily.pages.component;


/**
 * Class that represents the location of a {@link ComponentList}. This is a
 * backport from Verions 6.5.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4.1
 */
public class Location {

	private String path;

	private String key;

	public Location() {
	}

	public Location(Location location) {
		path = location.getPath();
		key = location.getKey();
	}

	public Location(String path, String key) {
		this.path = path;
		this.key = key;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Location) {
			return toString().equals(obj.toString());
		}
		return false;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return "/" + path + '#' + key;
	}
}
