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
package org.riotfamily.pages.config;

import org.springframework.util.Assert;

public class PageTypeRef extends ContentPageType {

	@Override
	public void register(SitemapSchema schema, PageType parent) {
		PageType ref = schema.getPageType(getName());
		Assert.notNull(ref, "Referenced type not found: " + getName());
		Assert.isInstanceOf(ContentPageType.class, ref, "Referenced type must be a ContentPageType");
		copyFrom((ContentPageType) ref);
		setParent(parent);
	}
	
	private void copyFrom(ContentPageType ref) {
		setLabel(ref.getLabel());
		setHandler(ref.getHandler());
		setChildTypes(ref.getChildTypes());
	}
	
}
