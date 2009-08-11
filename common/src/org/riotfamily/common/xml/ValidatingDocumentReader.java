package org.riotfamily.common.xml;

import org.springframework.beans.factory.xml.PluggableSchemaResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;

/**
 * Convinience class to read and validate XML files with a DTD.
 */
public class ValidatingDocumentReader extends DocumentReader {

	public ValidatingDocumentReader(Resource resource) {
		super(resource);
	}
	
	protected int getValidationMode() {
		return XmlBeanDefinitionReader.VALIDATION_XSD;
	}

	protected EntityResolver getEntityResolver() {
		return new PluggableSchemaResolver(getClass().getClassLoader());
	}
}
