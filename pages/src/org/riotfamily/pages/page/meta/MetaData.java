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
package org.riotfamily.pages.page.meta;

public class MetaData {

	private String title;
	
	private String keywords;
	
	private String description;

	
	public MetaData() {
	}
	
	public MetaData(String title) {
		this.title = title;
	}

	public MetaData(String title, String keywords, String description) {
		this.title = title;
		this.keywords = keywords;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isComplete() {
		return title != null && keywords != null && description != null;
	}
	
	public void fillIn(MetaData defaults) {
		fillIn(defaults.getTitle(), defaults.getKeywords(), 
				defaults.getDescription());
	}
	
	public void fillIn(String defaultTitle, String defaultKeywords, 
			String defaultDescription) {
		
		if (title == null) {
			title = defaultTitle;
		}
		if (keywords == null) {
			keywords = defaultKeywords;
		}
		if (description == null) {
			description = defaultDescription;
		}
	}
	
}
