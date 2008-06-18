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
package org.riotfamily.common.web.dwr;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.directwebremoting.Container;
import org.directwebremoting.extend.AccessControl;
import org.directwebremoting.extend.Configurator;
import org.directwebremoting.extend.Converter;
import org.directwebremoting.extend.ConverterManager;
import org.directwebremoting.extend.Creator;
import org.directwebremoting.extend.CreatorManager;
import org.directwebremoting.impl.SignatureParser;
import org.directwebremoting.spring.BeanCreator;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.StringUtils;

public class SpringConfigurator implements Configurator {

	private Map<String, Object> serviceBeans;

	private Class<?>[] serviceInterfaces;

	private Properties converterTypes;
	
	private Map<String, Converter> converters;

	private String signatures;

	/**
	 * Sets a map of beans to be exported keyed by their script name.
	 * @param serviceBeans Map of beans to export
	 */
	public void setServiceBeans(Map<String, Object> serviceBeans) {
		this.serviceBeans = serviceBeans;
	}

	/**
	 * Sets the interfaces to be exported. This is a convenient way
	 * to control which methods should be exposed to the client. This is
	 * especially useful when your service beans are AOP proxies.
	 * If no interfaces are configured only the default access rules apply.
	 * @param serviceInterfaces Interfaces to export
	 */
	public void setServiceInterfaces(Class<?>[] serviceInterfaces) {
		this.serviceInterfaces = serviceInterfaces;
	}

	public void setConverters(Map<String, Converter> converters) {
		this.converters = converters;
	}
	
	public void setConverterTypes(Properties converterTypes) {
		this.converterTypes = converterTypes;
	}

	public void setSignatures(String signatures) {
		this.signatures = signatures;
	}

	public void configure(Container container) {
		ConverterManager converterManager = (ConverterManager)
				container.getBean(ConverterManager.class.getName());

		if (converters != null) {
        	Iterator<Map.Entry<String, Converter>> it = converters.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Converter> entry = it.next();
				String match = entry.getKey();
				Converter converter = entry.getValue();
				converter.setConverterManager(converterManager);
				converterManager.addConverter(match, converter);
			}
        }
		
        if (converterTypes != null) {
			Iterator it = converterTypes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
				String match = entry.getKey();
				String type = entry.getValue();
				try {
					converterManager.addConverter(match, type, Collections.EMPTY_MAP);
				}
				catch (IllegalArgumentException e) {
					throw new BeanCreationException("Error adding converter", e);
				}
				catch (InstantiationException e) {
					throw new BeanCreationException("Error adding converter", e);
				}
				catch (IllegalAccessException e) {
					throw new BeanCreationException("Error adding converter", e);
				}
			}
		}
                
        CreatorManager creatorManager = (CreatorManager)
        		container.getBean(CreatorManager.class.getName());


        if (serviceBeans != null) {
			Iterator<Map.Entry<String, Object>> it = serviceBeans.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = it.next();
				String scriptName = entry.getKey();
				BeanCreator creator = new BeanCreator();
				creator.setScope(Creator.APPLICATION);
				creator.setBean(entry.getValue());
				creator.afterPropertiesSet();
				creatorManager.addCreator(scriptName, creator);
			}
		}

        if (serviceInterfaces != null) {
        	AccessControl accessControl = (AccessControl) container.getBean(
        		AccessControl.class.getName());

        	for (int i = 0; i < serviceInterfaces.length; i++) {
        		includeMethods(accessControl, serviceInterfaces[i]);
        	}
        }

        if (StringUtils.hasText(signatures)) {
            SignatureParser sigp = new SignatureParser(converterManager, creatorManager);
            sigp.parse(signatures);
        }
	}

	private void includeMethods(AccessControl accessControl,
			Class<?> serviceInterface) {

		Iterator<Map.Entry<String, Object>> it = serviceBeans.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if (serviceInterface.isInstance(entry.getValue())) {
				String scriptName = entry.getKey();
				includeMethods(accessControl, serviceInterface, scriptName);
			}
		}
	}

	private void includeMethods(AccessControl accessControl,
			Class<?> serviceInterface, String scriptName) {

		Method[] methods = serviceInterface.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			accessControl.addIncludeRule(scriptName, methods[i].getName());
		}
	}

}
