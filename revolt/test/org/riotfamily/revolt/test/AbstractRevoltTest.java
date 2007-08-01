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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.revolt.test;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.PluggableSchemaResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.xml.sax.InputSource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class AbstractRevoltTest 
		extends AbstractDependencyInjectionSpringContextTests {

	protected JdbcTemplate jdbcTemplate;
	
	/**
	 * Setter: DataSource is provided by Dependency Injection.
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
		
	protected ConfigurableApplicationContext createApplicationContext(String[] locations) {
		GenericApplicationContext context = new GenericApplicationContext();
		customizeBeanFactory(context.getDefaultListableBeanFactory());
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
		
		String location = "org/riotfamily/revolt/test/namespace.properties";
		NamespaceHandlerResolver resolver = new DefaultNamespaceHandlerResolver(getClass().getClassLoader(), location);
		reader.setNamespaceHandlerResolver(resolver);
		reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
		reader.setEntityResolver(new DummySchemaResolver());
		reader.loadBeanDefinitions(locations);
		context.refresh();
		return context;
	}
	
	private final class DummySchemaResolver extends PluggableSchemaResolver {

		public DummySchemaResolver() {
			super(AbstractRevoltTest.this.getClass().getClassLoader());
		}

		public InputSource resolveEntity(String publicId, String systemId) throws IOException {
			InputSource source = super.resolveEntity(publicId, systemId);
			if (source == null) {
				Resource resource = new ClassPathResource("org/riotfamily/revolt/config/revolt.xsd");
				source = new InputSource(resource.getInputStream());
				source.setPublicId(publicId);
				source.setSystemId(systemId);
			}
			return source;
		}
	}
}
