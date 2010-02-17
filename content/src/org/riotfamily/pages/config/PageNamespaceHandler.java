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

import org.riotfamily.common.beans.namespace.ChildDecorator;
import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.namespace.ListItemDecorator;
import org.riotfamily.common.beans.namespace.MapEntryDecorator;
import org.riotfamily.common.beans.namespace.PropertyDecorator;
import org.riotfamily.common.beans.namespace.PropertyValueDecorator;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * NamespaceHandler that handles the <code>page</code> namespace as
 * defined in <code>page.xsd</code> which can be found in the same package.
 */
public class PageNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("schema", SitemapSchema.class)
				.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR)
				.setDecorator(new PropertyDecorator("rootPage"));
		
		ChildDecorator typeDecorator = new ChildDecorator()
				.register("handler", new PropertyDecorator())
				.register("type", new ListItemDecorator("childTypes"))
				.register("type-ref", new ListItemDecorator("childTypes"))
				.register("prop", new MapEntryDecorator("properties", "key"))
				.register("system-page", new ListItemDecorator("childPages"))
				.register("virtual-page", new PropertyValueDecorator("virtualPage"));
				
		register("type", ContentPageType.class).addReference("handler").setDecorator(typeDecorator);
		register("root-page", RootPageType.class).addReference("handler").setDecorator(typeDecorator);
		register("system-page", SystemPageType.class).addReference("handler").setDecorator(typeDecorator);
		
		register("virtual-page", VirtualPageType.class).addReference("handler").setDecorator(new ChildDecorator()
				.register("handler", new PropertyDecorator())
				.register("resolver", new PropertyDecorator())
				.register("virtual-page", new PropertyValueDecorator("child")));
		
		register("type-ref", PageTypeRef.class);
	}
	
}
