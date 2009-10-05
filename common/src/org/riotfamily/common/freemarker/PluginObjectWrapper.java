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
package org.riotfamily.common.freemarker;

import java.util.Collection;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PluginObjectWrapper extends DefaultObjectWrapper {

	private Collection<ObjectWrapperPlugin> plugins;
	
	public PluginObjectWrapper(Collection<ObjectWrapperPlugin> plugins) {
		this.plugins = plugins;
	}
	
	public TemplateModel wrap(Object obj) throws TemplateModelException {
		if (obj == null) {
			return null;
		}
		if (obj instanceof TemplateModel) {
			return (TemplateModel) obj;
		}
		for (ObjectWrapperPlugin plugin : plugins) {
			if (plugin.supports(obj)) {
				return plugin.wrapSupportedObject(obj, this);
			}
		}
		return wrapUnsupportedObject(obj);
	}
	
	public TemplateModel wrapUnsupportedObject(Object obj) 
			throws TemplateModelException {
		
		return super.wrap(obj);
	}
	
}
