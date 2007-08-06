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
package org.riotfamily.pages;

import java.io.Serializable;
import java.util.Locale;


/**
 * Class that specifies the location of a Page. A page or alias can be uniquely
 * identified using a path and a locale.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class PageLocation implements Serializable {

	private String siteName;

	private String path;

	private Locale locale;

	public PageLocation() {
	}

	public PageLocation(String siteName, String path, Locale locale) {
		this.siteName = siteName;
		this.path = path;
		this.locale = locale;
	}

	public PageLocation(Page page) {
		this.siteName = page.getNode().getSite().getName();
		this.path = page.getPath();
		this.locale = page.getLocale();
	}

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String toString() {
		return siteName + ":" + locale + ":" + path;
	}

	public boolean equals(Object obj) {
		if (obj instanceof PageLocation) {
			return toString().equals(obj.toString());
		}
		return false;
	}

	public int hashCode() {
		return toString().hashCode();
	}

}
