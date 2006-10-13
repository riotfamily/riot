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
