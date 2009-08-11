package org.riotfamily.pages.config;

import org.riotfamily.common.beans.namespace.ChildDecorator;
import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.namespace.ListItemDecorator;
import org.riotfamily.common.beans.namespace.MapEntryDecorator;
import org.riotfamily.common.beans.namespace.PropertyDecorator;
import org.riotfamily.common.beans.namespace.PropertyValueDecorator;

/**
 * NamespaceHandler that handles the <code>page</code> namespace as
 * defined in <code>page.xsd</code> which can be found in the same package.
 */
public class PageNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("schema", SitemapSchema.class).setDecorator(
				new PropertyDecorator("rootPage"));
		
		ChildDecorator typeDecorator = new ChildDecorator()
				.register("bean", new PropertyValueDecorator("handler"))
				.register("ref", new PropertyValueDecorator("handler"))
				.register("type", new ListItemDecorator("childTypes"))
				.register("type-ref", new ListItemDecorator("childTypes"))
				.register("prop", new MapEntryDecorator("properties", "key"))
				.register("system-page", new ListItemDecorator("childPages"));
				
		register("type", PageType.class).setDecorator(typeDecorator);
		register("root-page", RootPage.class).setDecorator(typeDecorator);
		register("system-page", SystemPage.class).setDecorator(typeDecorator);
		
		register("type-ref", PageTypeRef.class);
	}
	
}
