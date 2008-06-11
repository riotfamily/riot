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

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.element.select.SelectBox;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.setup.HandlerNameHierarchy;
import org.riotfamily.riot.form.ui.FormUtils;

/**
 * FormInitializer that imports form fields defined in content-forms.xml.
 * If a new page is edited, the {@link HandlerNameHierarchy} is asked for
 * possible handler-names. If more than one handler-name is configured, a
 * dropdown is added that lets the user select a page-type.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.6
 */
public class PageFormInitializer implements FormInitializer {

	private HandlerNameHierarchy handlerNameHierarchy;
	
	private FormRepository repository;

	public PageFormInitializer(HandlerNameHierarchy handlerNameHierarchy, 
			FormRepository repository) {
		
		this.handlerNameHierarchy = handlerNameHierarchy;
		this.repository = repository;
	}

	public void initForm(Form form) {
		String handlerName = null;
		SelectBox sb = null;
		if (form.isNew())  {
			Page parentPage = null;
			Object parent = FormUtils.loadParent(form);
			if (parent instanceof Page) {
				parentPage = (Page) parent;
				//Make the parent id available for the InternalLinkField:
				form.setAttribute("pageId", parentPage.getId());
			}
			else if (parent instanceof Site) {
				//Make the site id available for the InternalLinkField:
				Site site = (Site) parent;
				form.setAttribute("siteId", site.getId());
			}
			String[] handlerNames = handlerNameHierarchy.getChildHandlerNameOptions(parentPage);
			if (handlerNames.length > 0) {
				sb = createHandlerNameBox(form, handlerNames);
				handlerName = handlerNames[0];
			}
			else {
				handlerName = "default";
			}
		}
		else {
			Page page = (Page) form.getBackingObject();
			form.setAttribute("pageId", page.getId());
			handlerName = page.getHandlerName();
		}
		
		PagePropertiesEditor ppe = new PagePropertiesEditor(repository, 
				getMasterPage(form), handlerName);
		
		if (sb != null) {
			sb.addChangeListener(ppe);
		}
		form.addElement(ppe, "pageProperties");
	}
	
	private SelectBox createHandlerNameBox(Form form, String[] handlerNames) {
		if (handlerNames.length > 1) {
			SelectBox sb = new SelectBox();
			sb.setRequired(true);
			sb.setOptions(handlerNames);
			sb.setLabelMessageKey("page.handlerName.");
			sb.setAppendLabel(true);
			NestedForm nodeForm = new NestedForm();
			nodeForm.setIndent(false);
			nodeForm.setRequired(true);
			form.addElement(nodeForm, "node");
			nodeForm.addElement(sb, "handlerName");
			return sb;
		}
		return null;
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
