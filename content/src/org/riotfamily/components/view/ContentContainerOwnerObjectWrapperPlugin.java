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
package org.riotfamily.components.view;

import org.riotfamily.common.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.common.web.support.RequestHolder;
import org.riotfamily.components.model.ContentContainerOwner;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapperPlugin that wraps objects which implement the 
 * {@link ContentContainerOwner} interface.
 * 
 * The plugin creates a {@link ContentContainerOwnerFacade} internally and wraps
 * it inside a {@link ContentFacadeTemplateModel}.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class ContentContainerOwnerObjectWrapperPlugin implements ObjectWrapperPlugin {

	public boolean supports(Object obj) {
		return obj instanceof ContentContainerOwner;
	}

	public TemplateModel wrapSupportedObject(Object obj, 
			PluginObjectWrapper wrapper) 
			throws TemplateModelException {
		
		ContentContainerOwner owner = (ContentContainerOwner) obj;
		
		ContentContainerOwnerFacade facade = new ContentContainerOwnerFacade(
				owner, RequestHolder.getRequest(), RequestHolder.getResponse());
		
		return new ContentFacadeTemplateModel(facade, wrapper);
	}

}
