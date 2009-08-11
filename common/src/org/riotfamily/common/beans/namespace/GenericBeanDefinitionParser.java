package org.riotfamily.common.beans.namespace;

import java.util.HashMap;
import java.util.HashSet;

import org.riotfamily.common.util.Generics;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class GenericBeanDefinitionParser extends AbstractGenericBeanDefinitionParser {

	public String aliasAttribute;

	private HashMap<String, String> translations = Generics.newHashMap();

	private HashSet<String> references = Generics.newHashSet();

	public GenericBeanDefinitionParser(Class<?> beanClass) {
		super(beanClass);
	}
	
	public GenericBeanDefinitionParser(String className) {
		super(className);
	}

	public GenericBeanDefinitionParser addTranslation(String attributeName,
			String property) {

		translations.put(attributeName, property);
		return this;
	}

	public GenericBeanDefinitionParser addReference(String attributeName) {
		references.add(extractPropertyName(attributeName));
		return this;
	}

	public void setAliasAttribute(String aliasAttribute) {
		this.aliasAttribute = aliasAttribute;
	}
	
	protected String resolveAlias(Element element, 
			AbstractBeanDefinition definition, ParserContext parserContext) {
		
		return aliasAttribute != null ? element.getAttribute(aliasAttribute) : null;
	}

	/**
	 * Parse the supplied {@link Element} and populate the supplied
	 * {@link BeanDefinitionBuilder} as required.
	 * <p>This implementation maps any attributes present on the
	 * supplied element to {@link org.springframework.beans.PropertyValue}
	 * instances, and
	 * {@link BeanDefinitionBuilder#addPropertyValue(String, Object) adds them}
	 * to the
	 * {@link org.springframework.beans.factory.config.BeanDefinition builder}.
	 * <p>The {@link #extractPropertyName(String)} method is used to
	 * reconcile the name of an attribute with the name of a JavaBean
	 * property.
	 * @param element the XML element being parsed
	 * @param parserContext the object encapsulating the current state of the parsing process
	 * @param builder used to define the <code>BeanDefinition</code>
	 * @see #extractPropertyName(String)
	 */
	protected final void doParse(Element element, 
			ParserContext parserContext, BeanDefinitionBuilder builder) {
		
		NamedNodeMap attributes = element.getAttributes();
		for (int x = 0; x < attributes.getLength(); x++) {
			Attr attribute = (Attr) attributes.item(x);
			String name = attribute.getLocalName();
			if (isEligibleAttribute(name, parserContext)) {
				String propertyName = extractPropertyName(name);
				Assert.state(StringUtils.hasText(propertyName),
						"Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");

				Object value;
				if (references.contains(propertyName)) {
					value = new RuntimeBeanReference(attribute.getValue());
				}
				else {
					value = attribute.getValue();
				}
				builder.addPropertyValue(propertyName, value);
			}
		}
		postProcess(builder, parserContext, element);
	}
	
	/**
	 * Determine whether the given attribute is eligible for being
	 * turned into a corresponding bean property value.
	 * <p>The default implementation considers any attribute as eligible,
	 * except for the "id" and "name" attributes in case of a top-level bean.
	 * @param attributeName the attribute name taken straight from the
	 * XML element being parsed (never <code>null</code>)
	 */
	protected boolean isEligibleAttribute(String attributeName, 
			ParserContext parserContext) {
		
		return parserContext.isNested() || (!attributeName.equals(ID_ATTRIBUTE) 
				&& !attributeName.equals(aliasAttribute));
	}

	/**
	 * Extract a JavaBean property name from the supplied attribute name.
	 * <p>The default implementation first looks for a translation set via
	 * {@link #addTranslation(String, String)}. If no translation is found,
	 * the {@link Conventions#attributeNameToPropertyName(String)}
	 * method to perform the extraction.
	 * <p>The name returned must obey the standard JavaBean property name
	 * conventions. For example for a class with a setter method
	 * '<code>setBingoHallFavourite(String)</code>', the name returned had
	 * better be '<code>bingoHallFavourite</code>' (with that exact casing).
	 * @param attributeName the attribute name taken straight from the
	 * XML element being parsed (never <code>null</code>)
	 * @return the extracted JavaBean property name (must never be <code>null</code>)
	 */
	protected String extractPropertyName(String attributeName) {
		String property = (String) translations.get(attributeName);
		if (property == null) {
			property = Conventions.attributeNameToPropertyName(attributeName);
		}
		return property;
	}

	/**
	 * Hook method that derived classes can implement to inspect/change a
	 * bean definition after parsing is complete.
	 * <p>The default implementation delegates to the <code>postProcess</code>
	 * version without ParserContext argument.
	 * @param beanDefinition the parsed (and probably totally defined) bean definition being built
	 * @param parserContext the object encapsulating the current state of the parsing process
	 * @param element the XML element that was the source of the bean definition's metadata
	 */
	protected void postProcess(BeanDefinitionBuilder beanDefinition, 
			ParserContext parserContext, Element element) {
		
		postProcess(beanDefinition, element);
	}
	
	/**
	 * Hook method that derived classes can implement to inspect/change a
	 * bean definition after parsing is complete.
	 * <p>The default implementation does nothing.
	 * @param beanDefinition the parsed (and probably totally defined) bean definition being built
	 * @param element the XML element that was the source of the bean definition's metadata
	 */
	protected void postProcess(BeanDefinitionBuilder beanDefinition, Element element) {
	}


}
