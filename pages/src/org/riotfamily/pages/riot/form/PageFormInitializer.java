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

import java.util.List;

import org.riotfamily.core.screen.form.FormUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.element.select.SelectBox;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.schema.SitemapSchema;
import org.riotfamily.pages.schema.TypeInfo;

/**
 * FormInitializer that imports form fields defined in content-forms.xml.
 * If a new page is edited, the {@link PageTypeHierarchy} is asked for
 * possible page types. If more than one page type is configured, a
 * dropdown is added that lets the user select a type.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.6
 */
public class PageFormInitializer implements FormInitializer {

	private SitemapSchema sitemapSchema;
	
	private FormRepository repository;

	public PageFormInitializer(SitemapSchema sitemapSchema, 
			FormRepository repository) {
		
		this.sitemapSchema = sitemapSchema;
		this.repository = repository;
	}

	public void initForm(Form form) {
		String pageType = null;
		SelectBox sb = null;
		if (form.isNew())  {
			Page parentPage = null;
			Object parent = FormUtils.loadParent(form);
			if (parent instanceof Page) {
				parentPage = (Page) parent;
				form.setAttribute("pageId", parentPage.getId());
				form.setAttribute("siteId", parentPage.getSite().getId());
			}
			else if (parent instanceof Site) {
				Site site = (Site) parent;
				form.setAttribute("siteId", site.getId());
			}
			List<TypeInfo> pageTypes = sitemapSchema.getChildTypeOptions(parentPage);
			if (pageTypes != null) {
				sb = createPageTypeBox(form, pageTypes);
				pageType = pageTypes.get(0).getName();
			}
			else {
				pageType = "default";
			}
		}
		else {
			Page page = (Page) form.getBackingObject();
			form.setAttribute("pageId", page.getId());
			form.setAttribute("siteId", page.getSite().getId());
			pageType = page.getPageType();
		}
		
		PagePropertiesEditor ppe = new PagePropertiesEditor(repository, form, pageType);
		
		if (sb != null) {
			sb.addChangeListener(ppe);
		}
		form.addElement(ppe, "pageProperties");
	}
	
	private SelectBox createPageTypeBox(Form form, List<TypeInfo> pageTypes) {
		if (pageTypes.size() > 1) {
			SelectBox sb = new SelectBox();
			sb.setRequired(true);
			sb.setOptions(pageTypes);
			sb.setLabelProperty("name");
			sb.setLabelMessageKey("page.pageType.");
			sb.setAppendLabel(true);
			form.addElement(sb, "pageType");
			return sb;
		}
		return null;
	}
	
}
