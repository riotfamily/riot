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
package org.riotfamily.pages.riot.form;

import java.util.Iterator;

import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.form.ui.FormUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.6
 */
public class PageFormInitializer implements FormInitializer {

	private PageDao pageDao;
	
	private FormRepository repository;

	public PageFormInitializer(PageDao pageDao, 
			FormRepository repository) {
		
		this.pageDao = pageDao;
		this.repository = repository;
	}

	public void initForm(Form form) {
		String handlerName = null;
		if (form.isNew())  {
			Object parent = FormUtils.loadParent(form);
			if (parent instanceof Page) {
				Page parentPage = (Page) parent;
				handlerName = pageDao.getChildHandlerName(parentPage.getHandlerName());
			}
		}
		else {
			Page page = (Page) form.getBackingObject();
			handlerName = page.getHandlerName();
		}
		if (handlerName == null) {
			handlerName = "default";
		}
		addComponentFormElements(form, handlerName + "-page");
	}
	
	private void addComponentFormElements(Form form, String id) {
		boolean present = false;
		NestedForm nestedForm = new NestedForm();
		nestedForm.setRequired(true);
		nestedForm.setIndent(false);
		nestedForm.setEditorBinder(new PagePeopertiesEditorBinder());
		nestedForm.setStyleClass(id);
		Page masterPage = getMasterPage(form);
		if (masterPage == null) {
			present |= addPagePropertyEditors(form, nestedForm, 
					"master-" + id, null);
		}
		present |= addPagePropertyEditors(form, nestedForm, id, masterPage);
		if (present) {
			form.addElement(nestedForm, "pageProperties");
		}
	}
	
	private boolean addPagePropertyEditors(Form form, NestedForm nestedForm, String id, Page masterPage) {
		boolean present = false;
		if (repository.containsForm(id)) {
			FormFactory factory = repository.getFormFactory(id);
			Iterator it = factory.getChildFactories().iterator();
			while (it.hasNext()) {
				ElementFactory ef = (ElementFactory) it.next();
				nestedForm.addElement(new PagePropertyEditor(ef, masterPage));
				present = true;
			}
		}
		return present;
	}
	
	
	private Page getMasterPage(Form form) {
		Page page = (Page) form.getBackingObject();
		if (page.getNode() != null) {
			Site site = page.getSite();
			if (site == null) {
				Object parent = FormUtils.loadParent(form);
				if (parent instanceof Page) {
					site = ((Page) parent).getSite();
				}
				else if (parent instanceof Site) {
					site = (Site) parent;
				}
			}
			if (site != null) {
				Site masterSite = site.getMasterSite();
				if (masterSite != null) {
					return page.getNode().getPage(masterSite);
				}
			}
		}
		return null;
	}

}
