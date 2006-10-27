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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.mvc.config;

import org.riotfamily.common.beans.xml.GenericBeanDefinitionParser;
import org.riotfamily.pages.mvc.GenericController;
import org.riotfamily.pages.mvc.hibernate.CurrentDateResolver;
import org.riotfamily.pages.mvc.hibernate.CurrentLocaleResolver;
import org.riotfamily.pages.mvc.hibernate.DateParameterResolver;
import org.riotfamily.pages.mvc.hibernate.DefaultParameterResolver;
import org.riotfamily.pages.mvc.hibernate.HqlModelBuilder;
import org.riotfamily.pages.mvc.hibernate.PagedHqlModelBuilder;
import org.riotfamily.pages.mvc.hibernate.RiotPrincipalResolver;
import org.riotfamily.pages.mvc.hibernate.StringToPrimitiveResolver;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * NamespaceHandler that handles the <code>mvc</code> namspace as
 * defined in <code>mvc.xsd</code> which can be found in the same package.
 */
public class MvcNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		GenericBeanDefinitionParser parser = new GenericBeanDefinitionParser();
		
		parser.registerElement("generic-controller", GenericController.class);
		parser.registerElement("hql", HqlModelBuilder.class);
		parser.registerElement("paged-hql", PagedHqlModelBuilder.class);
		parser.registerElement("attribute", DefaultParameterResolver.class);
		parser.registerElement("current-date", CurrentDateResolver.class);
		parser.registerElement("current-locale", CurrentLocaleResolver.class);
		parser.registerElement("riot-principal", RiotPrincipalResolver.class);
		parser.registerElement("string-to-primitive", 
				StringToPrimitiveResolver.class);
		
		parser.registerElement("date", DateParameterResolver.class);
		
		registerBeanDefinitionParser("generic-controller", parser);
		registerBeanDefinitionParser("hql", parser);
		registerBeanDefinitionParser("paged-hql", parser);
	}

}
