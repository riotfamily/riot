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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.pages.model.Page;

/**
 * @author Alf Werder <alf dot werder at artundweise dot de>
 * @since 7.0
 */
@Entity
@DiscriminatorValue("Page")
@SecondaryTable(
	name="riot_page_wrappers", 
	pkJoinColumns=@PrimaryKeyJoinColumn(name="wrapper_id")
)
public class PageWrapper extends ValueWrapper<Page> {
	
	private Page value;
	
	public PageWrapper() {
	}
	
	public PageWrapper(Page page) {
		this.value = page;
	}

	@ManyToOne
	@JoinColumn(table="riot_page_wrappers", name="id")
	@OnDelete(action=OnDeleteAction.CASCADE)
	@NotFound(action=NotFoundAction.IGNORE)
	public Page getValue() {
		return this.value;
	}

	public void setValue(Page value) {
		this.value = value;
	}
	
	public PageWrapper deepCopy() {
		return new PageWrapper(value);
	}
}
