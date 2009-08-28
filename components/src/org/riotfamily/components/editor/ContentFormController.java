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
package org.riotfamily.components.editor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentFragment;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Controller that displays a form to edit the properties of a ComponentVersion.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ContentFormController extends AbstractFrontOfficeFormController {

	public ContentFormController(FormContextFactory formContextFactory,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager) {
		
		super(formContextFactory, formRepository, transactionManager);
	}

	@SuppressWarnings("unchecked")
	protected void initForm(Form form, HttpServletRequest request) {
		super.initForm(form, request);
		Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			form.setAttribute(name, request.getParameter(name));
		}
	}
	
	protected Object getFormBackingObject(HttpServletRequest request) {
		return Content.loadFragment((String) request.getAttribute("contentId"));
	}
	
	protected Object update(Object object, HttpServletRequest request) {
		ContentFragment fragment = (ContentFragment) object;
		return fragment.getContent().merge();
	}

}
