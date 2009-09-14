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
package org.riotfamily.pages.model;

import org.riotfamily.core.dao.InvalidPropertyValueException;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class DuplicatePathComponentException extends InvalidPropertyValueException {
	
	private static final String CODE_NAME = "duplicate";

	private static final String FIELD_NAME = "pathComponent";

	private Page page;

	public DuplicatePathComponentException(Page page) {
		super(FIELD_NAME, CODE_NAME, new String[] { page.getPathComponent() },
				"There's already another page using the pathComponent.");
		
		this.page = page;
	}

	public Page getPage() {
		return this.page;
	}

}
