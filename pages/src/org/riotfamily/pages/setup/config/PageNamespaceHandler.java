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
package org.riotfamily.pages.setup.config;

import org.riotfamily.common.beans.xml.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.xml.ListItemDecorator;
import org.riotfamily.common.beans.xml.MapEntryDecorator;
import org.riotfamily.pages.dao.PageDefinition;
import org.riotfamily.pages.setup.PageSetupBean;

/**
 * NamespaceHandler that handles the <code>page</code> namspace as
 * defined in <code>page.xsd</code> which can be found in the same package.
 */
public class PageNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("setup", PageSetupBean.class)
				.addTranslation("tx", "transactionManager")
				.addTranslation("dao", "pageDao")
				.addReference("dao")
				.addReference("tx");

		register("site", SiteDefinition.class, 
				new ListItemDecorator("siteDefinitions"))
				.addTranslation("host", "hostName")
				.addTranslation("prefix", "pathPrefix")
				.setAliasAttribute(null);
		
		register("page", PageDefinition.class,
				new ListItemDecorator("pageDefinitions"))
				.addTranslation("system", "systemNode");

		registerBeanDefinitionDecorator("prop", new MapEntryDecorator("properties", "key"));
		
		register("handler-name", ChildHandlerNameDefinition.class);
	}
	
}
