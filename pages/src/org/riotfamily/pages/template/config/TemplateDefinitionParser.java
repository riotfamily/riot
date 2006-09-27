package org.riotfamily.pages.template.config;

import java.util.HashMap;
import java.util.Iterator;

import org.riotfamily.common.beans.xml.DefinitionParserUtils;
import org.riotfamily.pages.template.TemplateController;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class TemplateDefinitionParser implements BeanDefinitionParser {

	private static final String ID_ATTRIBUTE = "id";
	
	private static final String NAME_ATTRIBUTE = "name";
	
	private static final String PARENT_ATTRIBUTE = "parent";
	
	private static final String VIEW_NAME_ATTRIBUTE = "view-name";
	
	private static final String SESSION_ATTRIBUTE = "session";
	
	private static final String INSERT_TAG = "insert";
	
	private static final String REMOVE_TAG = "remove";
	
	private static final String SLOT_ATTRIBUTE = "slot";
	
	private static final String URL_ATTRIBUTE = "url";

	private static final String CONFIGURATION_PROPERTY = "configuration";
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		
		RootBeanDefinition definition = new RootBeanDefinition();
		definition.setBeanClass(TemplateController.class);
		
		DefinitionParserUtils.registerBeanDefinition(definition, element, 
				ID_ATTRIBUTE, NAME_ATTRIBUTE, parserContext);
		
		MutablePropertyValues pv = new MutablePropertyValues();
		
		DefinitionParserUtils.addReference(pv, element, PARENT_ATTRIBUTE);
		DefinitionParserUtils.addString(pv, element, VIEW_NAME_ATTRIBUTE);
		DefinitionParserUtils.addString(pv, element, SESSION_ATTRIBUTE);
	
		HashMap configuration = new HashMap();
		
		Iterator it = DomUtils.getChildElementsByTagName(
				element, INSERT_TAG).iterator();
		
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String slot = ele.getAttribute(SLOT_ATTRIBUTE);
			String url = ele.getAttribute(URL_ATTRIBUTE);
			configuration.put(slot, url);
		}
		
		it = DomUtils.getChildElementsByTagName(
				element, REMOVE_TAG).iterator();
		
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String slot = ele.getAttribute(SLOT_ATTRIBUTE);
			configuration.put(slot, null);
		}
		
		if (!configuration.isEmpty()) {
			pv.addPropertyValue(CONFIGURATION_PROPERTY, configuration);
		}
		
		definition.setPropertyValues(pv);
		
		return definition;
	}

}
