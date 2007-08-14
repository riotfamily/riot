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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.generic.config;


import org.riotfamily.common.beans.xml.GenericNamespaceHandlerSupport;
import org.riotfamily.common.beans.xml.NestedListDecorator;
import org.riotfamily.common.beans.xml.NestedPropertyDecorator;
import org.riotfamily.website.generic.GenericController;
import org.riotfamily.website.generic.GenericViewController;
import org.riotfamily.website.generic.model.hibernate.CurrentDateResolver;
import org.riotfamily.website.generic.model.hibernate.CurrentLanguageResolver;
import org.riotfamily.website.generic.model.hibernate.CurrentLocaleResolver;
import org.riotfamily.website.generic.model.hibernate.SplitDateParameterResolver;
import org.riotfamily.website.generic.model.hibernate.DefaultParameterResolver;
import org.riotfamily.website.generic.model.hibernate.HqlModelBuilder;
import org.riotfamily.website.generic.model.hibernate.PagedHqlModelBuilder;
import org.riotfamily.website.generic.model.hibernate.RiotUserResolver;
import org.riotfamily.website.generic.model.hibernate.StringToPrimitiveResolver;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;

/**
 * NamespaceHandler that handles the <code>generic</code> namspace as
 * defined in <code>generic.xsd</code> which can be found in the same package.
 */
public class GenericNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("controller", GenericController.class).addReference("model-builder");
		register("view", GenericViewController.class);

		BeanDefinitionDecorator setModelBuilder = new NestedPropertyDecorator("modelBuilder");
		register("hql", HqlModelBuilder.class, setModelBuilder).addTranslation("time-to-live", "ttlPeriod");
		register("paged-hql", PagedHqlModelBuilder.class, setModelBuilder).addTranslation("time-to-live", "ttlPeriod");
		registerSpringBeanDefinitionParser("model-builder", setModelBuilder);

		BeanDefinitionDecorator addParameterResolver = new NestedListDecorator("parameterResolvers");
		register("attribute", DefaultParameterResolver.class, addParameterResolver);
		register("current-date", CurrentDateResolver.class, addParameterResolver);
		register("current-locale", CurrentLocaleResolver.class, addParameterResolver);
		register("current-language", CurrentLanguageResolver.class, addParameterResolver);
		register("riot-user", RiotUserResolver.class, addParameterResolver);
		register("string-to-primitive", StringToPrimitiveResolver.class, addParameterResolver);
		register("split-date", SplitDateParameterResolver.class, addParameterResolver);
		registerSpringBeanDefinitionParser("custom-resolver", addParameterResolver);
	}

}
