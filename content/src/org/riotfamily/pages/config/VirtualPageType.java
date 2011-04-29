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

import java.util.Collection;

import org.riotfamily.common.util.Generics;
import org.riotfamily.pages.mapping.ChildPageResolver;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


public class VirtualPageType extends AbstractPageType implements VirtualPageParent {

	private VirtualPageType child;
	
	private ChildPageResolver resolver;
	
	public void setChild(VirtualPageType child) {
		this.child = child;
	}
	
	public VirtualPageType getChild() {
		return child;
	}
	
	public int getDepth() {
		return (child != null ? child.getDepth() : 0) + 1;
	}
	
	public void setResolver(ChildPageResolver resolver) {
		this.resolver = resolver;
	}
	
	public ChildPageResolver getResolver() {
		return resolver;
	}
	
	public VirtualPageType getVirtualChildType() {
		return child;
	}

	@Override
	public void register(SitemapSchema schema, PageType parent) {
		Assert.notNull(resolver, "A resolver must be set");
		super.register(schema, parent);
		if (child != null) {
			child.register(schema, this);
		}
	}
	
	public Page resolve(ContentPage parent, String pathTail) {
		String[] path = StringUtils.tokenizeToStringArray(pathTail, "/");
		if (getDepth() < path.length) {
			return null;
		}
		return resolve(parent, path, 0);
	}
	
	private Page resolve(Page parent, String[] path, int i) {
		Page page = resolver.resolvePage(this, parent, path[i]);
		
		if (path.length > i+1 && child != null) {
			return child.resolve(page, path, i+1);
		}
		return page;
	}
	
	public Collection<Page> listChildren(Page parent) {
		return Generics.emptyIfNull(resolver.listChildren(this, parent));
	}
	
	public Page getPage(Site site, Object object) {
		return resolver.getPage(this, site, object);
	}

}
