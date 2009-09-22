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
package org.riotfamily.pages.view;

import org.riotfamily.common.freemarker.FacadeTemplateModel;
import org.riotfamily.common.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.common.servlet.RequestHolder;
import org.riotfamily.pages.model.Page;
import org.springframework.core.Ordered;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageObjectWrapperPlugin implements ObjectWrapperPlugin, Ordered {

	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
	
	public boolean supports(Object obj) {
		return obj instanceof Page;
	}

	public TemplateModel wrapSupportedObject(Object obj, 
			PluginObjectWrapper wrapper) 
			throws TemplateModelException {
		
		Page page = (Page) obj;
		PageFacade facade = new PageFacade(page, RequestHolder.getRequest());
		return new FacadeTemplateModel(facade, page, wrapper);
	}

}
