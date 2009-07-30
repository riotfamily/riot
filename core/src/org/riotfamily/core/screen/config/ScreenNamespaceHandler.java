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
package org.riotfamily.core.screen.config;

import org.riotfamily.common.beans.namespace.ChildDecorator;
import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.namespace.ListDecorator;
import org.riotfamily.common.beans.namespace.ListItemDecorator;
import org.riotfamily.common.beans.namespace.PropertyDecorator;
import org.riotfamily.common.beans.namespace.PropertyValueDecorator;
import org.riotfamily.core.screen.GroupScreen;
import org.riotfamily.core.screen.form.FormScreen;
import org.riotfamily.core.screen.list.ColumnConfig;
import org.riotfamily.core.screen.list.TreeListScreen;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * NamespaceHandler that handles the <code>screen</code> namespace as
 * defined in <code>screen.xsd</code> which can be found in the same package.
 */
public class ScreenNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("group", GroupScreen.class).setDecorator(new ChildDecorator()
				.register("screenlets", new ListDecorator())
				.setDefault(new ListItemDecorator("childScreens")));
		
		register("form", FormScreen.class)
				.addTranslation("id", "beanName")
				.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR)
				.setDecorator(new ChildDecorator()
				.register("screenlets", new ListDecorator())
				.setDefault(new ListItemDecorator("childScreens")));
		
		register("list", TreeListScreen.class).setDecorator(new ChildDecorator()
				.register("dao", new PropertyDecorator())
				.register("columns", new ListDecorator())
				.register("commands", new ListDecorator())
				.register("screenlets", new ListDecorator())
				.setDefault(new PropertyValueDecorator("itemScreen")));
		
		register("column", ColumnConfig.class).setDecorator(
				new PropertyDecorator("renderer"));
	}
	
}
