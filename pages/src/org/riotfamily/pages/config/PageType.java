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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.config;

import java.util.List;

import org.riotfamily.common.util.FormatUtils;

public class PageType {

	private String name;
	
	private String label;
	
	private List<PageType> childTypes;
	
	private Object handler;
	
	private List<String> suffixes;

	public PageType() {
	}

	public PageType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		if (label == null) {
			label = FormatUtils.xmlToTitleCase(name);
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}
	
	public List<String> getSuffixes() {
		return suffixes;
	}

	public void setSuffixes(List<String> suffixes) {
		this.suffixes = suffixes;
	}
	
	public void setSuffix(String suffix) {
		this.suffixes = FormatUtils.tokenizeCommaDelimitedList(suffix);
	}

	public List<PageType> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(List<PageType> childTypes) {
		this.childTypes = childTypes;
	}
	
	void register(SitemapSchema schema) {
		schema.addType(this);
		if (childTypes != null) {
			for (PageType type : childTypes) {
				type.register(schema);
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof PageType) {
			PageType other = (PageType) obj;
			return name.equals(other.name);
		}
		return false;
	}
}
