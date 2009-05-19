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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.schema;

import org.riotfamily.common.beans.xml.ContextualBeanDecorator;
import org.riotfamily.common.beans.xml.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.xml.ListItemDecorator;
import org.riotfamily.common.beans.xml.MapEntryDecorator;
import org.riotfamily.common.beans.xml.NestedPropertyDecorator;

/**
 * NamespaceHandler that handles the <code>page</code> namspace as
 * defined in <code>page.xsd</code> which can be found in the same package.
 */
public class PageNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("schema", SitemapSchema.class);
		register("page", SystemPage.class, new ListItemDecorator("pages"));
		registerBeanDefinitionDecorator("prop", new MapEntryDecorator("properties", "key"));
		register("type", TypeInfo.class, 
				new ContextualBeanDecorator()
					.register("schema", new ListItemDecorator("types"))
					.register("type", new ListItemDecorator("childTypes"))
					.register("page", new NestedPropertyDecorator("type"))
		);
	}
	
}
