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
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view.freemarker;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.web.view.DynamicModel;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author alf Werder <alf dor werder at artundweise dot de>
 * @since 6.5
 */
public class DynamicModelWrapper implements ObjectWrapperPlugin {

	public boolean supports(Object obj) {
		return obj instanceof DynamicModel;
	}

	public TemplateModel wrapSupportedObject(Object obj,
		PluginObjectWrapper wrapper) throws TemplateModelException {
		
		return new TemplateDynamicModel(wrapper, (DynamicModel) obj);
	}

	private class TemplateDynamicModel implements TemplateHashModel {
		private PluginObjectWrapper wrapper;
		private DynamicModel model;
		
		public TemplateDynamicModel(PluginObjectWrapper wrapper,
			DynamicModel model) {
			
			this.wrapper = wrapper;
			this.model = model;
		}

		public TemplateModel get(String key) throws TemplateModelException {
			if (PropertyUtils.findReadMethod(
				model.getClass(), key) != null) {
				
				return wrapper.wrap(PropertyUtils.getProperty(model, key));
			} else {
				return wrapper.wrap(model.get(key));
			}
		}

		public boolean isEmpty() throws TemplateModelException {
			return false;
		}
	}
}
