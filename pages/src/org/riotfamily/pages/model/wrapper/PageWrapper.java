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
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.model.wrapper;

import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.pages.model.Page;
import org.springframework.util.Assert;

/**
 * @author Alf Werder <alf dot werder at artundweise dot de>
 * @since 7.0
 */
public class PageWrapper extends ValueWrapper {
	private Page page;
	
	public PageWrapper() {}
	
	public PageWrapper(Page page) {
		this.page = page;
	}

	public ValueWrapper deepCopy() {
		return new PageWrapper(page);
	}

	public Object getValue() {
		return page;
	}

	public void setValue(Object value) {
		Assert.isInstanceOf(Page.class, value);
		
		page = (Page) value;
	}

	public Page getPage() {
		return this.page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
