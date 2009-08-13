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
import org.riotfamily.components.model.ContentMap;

import freemarker.ext.beans.MapModel;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapperPlugin that wraps {@link ContentMap}s with a {@link MapModel} 
 * (instead of a {@link SimpleHash}) so that getOwner(), getId() etc. can be 
 * accessed by a template.
 */
public class ContentMapObjectWrapperPlugin implements ObjectWrapperPlugin {

	public boolean supports(Object obj) {
		return obj instanceof ContentMap;
	}

	public TemplateModel wrapSupportedObject(Object obj,
			PluginObjectWrapper wrapper) throws TemplateModelException {
		
		return new MapModel((ContentMap) obj, wrapper);
	}

}
