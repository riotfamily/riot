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
import org.riotfamily.common.beans.xml.ListItemDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;

/**
 * NamespaceHandler that handles the <code>generic</code> namespace as
 * defined in <code>generic.xsd</code> which can be found in the same package.
 */
public class GenericNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("controller", "org.riotfamily.website.generic.GenericController");
		register("view", "org.riotfamily.website.generic.GenericViewController");

		BeanDefinitionDecorator setModelBuilder = new ListItemDecorator("modelBuilders");
		register("hql", "org.riotfamily.website.generic.model.hibernate.HqlModelBuilder", setModelBuilder)
				.addTranslation("time-to-live", "ttlPeriod")
				.addTranslation("query-cache", "useQueryCache")
				.addTranslation("hibernate-cache-region", "hibernateCacheRegion");
		
		register("hql-list", "org.riotfamily.website.generic.model.hibernate.HqlListModelBuilder", setModelBuilder)
				.addTranslation("time-to-live", "ttlPeriod")
				.addTranslation("max", "maxResults")
				.addTranslation("query-cache", "useQueryCache")
				.addTranslation("hibernate-cache-region", "hibernateCacheRegion");
		
		register("hql-paged-list", "org.riotfamily.website.generic.model.hibernate.HqlPagedListModelBuilder", setModelBuilder)
				.addTranslation("time-to-live", "ttlPeriod")
				.addTranslation("query-cache", "useQueryCache")
				.addTranslation("hibernate-cache-region", "hibernateCacheRegion");
		
		registerSpringBeanDefinitionParser("model-builder", setModelBuilder);

		BeanDefinitionDecorator addParameterResolver = new ListItemDecorator("parameterResolvers");
		register("attribute", "org.riotfamily.website.generic.model.hibernate.DefaultParameterResolver", addParameterResolver);
		register("current-date", "org.riotfamily.website.generic.model.hibernate.CurrentDateResolver", addParameterResolver);
		register("current-locale", "org.riotfamily.website.generic.model.hibernate.CurrentLocaleResolver", addParameterResolver);
		register("current-language", "org.riotfamily.website.generic.model.hibernate.CurrentLanguageResolver", addParameterResolver);
		register("riot-user", "org.riotfamily.website.generic.model.hibernate.RiotUserResolver", addParameterResolver);
		register("string-to-primitive", "org.riotfamily.website.generic.model.hibernate.StringToPrimitiveResolver", addParameterResolver);
		register("split-date", "org.riotfamily.website.generic.model.hibernate.SplitDateParameterResolver", addParameterResolver);
		registerSpringBeanDefinitionParser("custom-resolver", addParameterResolver);
	}

}
