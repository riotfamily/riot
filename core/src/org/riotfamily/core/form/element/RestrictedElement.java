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
package org.riotfamily.core.form.element;

import org.riotfamily.core.security.AccessController;
import org.riotfamily.forms.element.AbstractConditionalElement;

/**
 * @since 6.5
 */
public class RestrictedElement extends AbstractConditionalElement {
	
	@Override
	protected boolean isEditable() {
		return AccessController.isGranted("editProperty", 
				getEditor().getEditorBinding().getProperty(),
				getEditor());
	}
}
