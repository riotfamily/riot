/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.pages.riot.form;

import java.util.List;

import org.riotfamily.core.screen.form.FormScreen;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.element.select.SelectBox;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.config.PageType;
import org.riotfamily.pages.model.ContentPage;
import org.springframework.util.Assert;

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

	private FormRepository repository;

	public PageFormInitializer(FormRepository repository) {
		this.repository = repository;
	}

	public void initForm(Form form) {
		PageType pageType = null;
		SelectBox sb = null;
		if (form.isNew())  {
			Object parent = FormScreen.getScreenContext(form).getParent();
			ContentPage parentPage = (ContentPage) parent;
			form.setAttribute("pageId", parentPage.getId());
			form.setAttribute("siteId", parentPage.getSite().getId());
			addPathComponentField(form);
			
			List<? extends PageType> pageTypes = parentPage.getPageType().getChildTypes();
			Assert.notEmpty(pageTypes, "Sitemap schema does not allow the creation of pages here");
			sb = createPageTypeBox(form, pageTypes);
			pageType = pageTypes.get(0);
		}
		else {
			ContentPage page = (ContentPage) form.getBackingObject();
			form.setAttribute("pageId", page.getId());
			form.setAttribute("siteId", page.getSite().getId());
			if (page.getParent() != null) {
				addPathComponentField(form);
			}
			pageType = page.getPageType();
		}
		
		PagePropertiesEditor ppe = new PagePropertiesEditor(repository, form, pageType);
		
		if (sb != null) {
			sb.addChangeListener(ppe);
		}
		form.addElement(ppe, "contentContainer.previewVersion");
	}

	private void addPathComponentField(Form form) {
		TextField t = new TextField();
		t.setRequired(true);
		t.setRegex("([A-Za-z0-9_.,*@{}-]*)");
		form.addElement(t, "pathComponent");
	}
	
	private SelectBox createPageTypeBox(Form form, List<? extends PageType> pageTypes) {
		SelectBox sb = new SelectBox();
		sb.setRequired(true);
		sb.setHideIfEmpty(true);
		sb.setOptions(pageTypes);
		sb.setLabelProperty("label");
		form.addElement(sb, "pageType");
		return sb;
	}
	
}
