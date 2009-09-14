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
package org.riotfamily.website.form;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.view.MacroHelperFactory;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class FormMacroHelperFactory implements MacroHelperFactory {

	private static final String INSTANCE_ATTRIBUTE = FormMacroHelper.class.getName();

	/**
	 * Creates a {@link FormMacroHelper}. The helper is stored as request 
	 * attribute so that components can access the same instance. 
	 */
	public Object createMacroHelper(HttpServletRequest request, 
			HttpServletResponse response, Map<String, ?> model) {
	
		FormMacroHelper helper = (FormMacroHelper) request.getAttribute(INSTANCE_ATTRIBUTE);
		if (helper == null) {
			helper = new FormMacroHelper();
			request.setAttribute(INSTANCE_ATTRIBUTE, helper);
		}
		return helper;
	}

}
