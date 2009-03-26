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
package org.riotfamily.core.screen.form;

import java.util.Collection;
import java.util.List;

import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;

public class FormScreen extends FormScreenSupport implements BeanNameAware {

	private String id;
	
	private String formId;
	
	private RiotScreen parentScreen;
	
	private List<RiotScreen> childScreens;
	

	public FormScreen(FormContextFactory formContextFactory,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager) {
		
		super(formContextFactory, formRepository, transactionManager);
	}

	public String getFormId() {
		if (formId == null) {
			return getId();
		}
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
	
	public String getTitle(Object object) {
		if (object != null) {
			return ScreenUtils.getLabel(object, this);
		}
		return "*new*";
	}

	public void setBeanName(String beanName) {
		if (id == null) {
			id = beanName;
		}
	}
	
	// -----------------------------------------------------------------------
	// Implementation of the RiotScreen interface
	// -----------------------------------------------------------------------
	
	public Collection<RiotScreen> getChildScreens() {
		return childScreens;
	}

	public String getIcon() {
		return null;
	}

	public String getId() {
		return id;
	}

	public RiotScreen getParentScreen() {
		return parentScreen;
	}

	public void setParentScreen(RiotScreen parentScreen) {
		this.parentScreen = parentScreen;
	}
	
}
