package org.riotfamily.pages.template.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * NamespaceHandler that handles the <code>template</code> namespace, 
 * defined in <code>template.xsd</code>.
 * 
 * TODO Insert example XML code here
 */
public class TemplateNamespaceHandler extends NamespaceHandlerSupport {

	private static final String ELEMENT_NAME = "definition";
	
	public void init() {
		registerBeanDefinitionParser(ELEMENT_NAME, 
				new TemplateDefinitionParser());
	}

}
