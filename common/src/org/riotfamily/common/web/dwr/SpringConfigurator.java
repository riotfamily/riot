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
package org.riotfamily.common.web.dwr;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.directwebremoting.Container;
import org.directwebremoting.extend.AccessControl;
import org.directwebremoting.extend.Configurator;
import org.directwebremoting.extend.ConverterManager;
import org.directwebremoting.extend.Creator;
import org.directwebremoting.extend.CreatorManager;
import org.directwebremoting.impl.SignatureParser;
import org.directwebremoting.spring.BeanCreator;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.StringUtils;

public class SpringConfigurator implements Configurator {

	private Map serviceBeans;
	
	private Class[] serviceInterfaces;
	
	private Properties converterTypes;
	
	private String signatures;
	
	/**
	 * Sets a map of beans to be exported keyed by their script name.
	 * @param serviceBeans Map of beans to export
	 */
	public void setServiceBeans(Map serviceBeans) {
		this.serviceBeans = serviceBeans;
	}

	/**
	 * Sets the interfaces to be exported. This is a convenient way
	 * to control which methods should be exposed to the client. This is 
	 * especially useful when your service beans are AOP proxies.
	 * If no interfaces are configured only the default access rules apply.
	 * @param serviceInterfaces Interfaces to export
	 */
	public void setServiceInterfaces(Class[] serviceInterfaces) {
		this.serviceInterfaces = serviceInterfaces;
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
		        
        if (converterTypes != null) {
			Iterator it = converterTypes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String match = (String) entry.getKey();
				String type = (String) entry.getValue();
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
			Iterator it = serviceBeans.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String scriptName = (String) entry.getKey();
				BeanCreator creator = new BeanCreator();
				creator.setScope(Creator.APPLICATION);
				creator.setBean(entry.getValue());
				creatorManager.addCreator(scriptName, creator);
			}
		}
        
        if (serviceInterfaces != null) {
        	AccessControl accessControl = (AccessControl) container.getBean(
        		AccessControl.class.getName());
        	
        	for (int i = 0; i < serviceInterfaces.length; i++) {
        		String scriptName = findScriptNameForInterface(serviceInterfaces[i]);
        		Method[] methods = serviceInterfaces[i].getDeclaredMethods();
        		for (int m = 0; m < methods.length; m++) {
        			accessControl.addIncludeRule(scriptName, methods[m].getName());			
        		}
        	}
        }
        
        if (StringUtils.hasText(signatures)) {
            SignatureParser sigp = new SignatureParser(converterManager, creatorManager);
            sigp.parse(signatures);
        }
	}

	/**
	 * Returns the script name of the first service bean that implements the
	 * given interface.
	 */
	protected String findScriptNameForInterface(Class serviceInterface) {
		Iterator it = serviceBeans.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (serviceInterface.isInstance(entry.getValue())) {
				return (String) entry.getKey(); 
			}
		}
		throw new BeanCreationException("No serviceBean found that implements "
				+ serviceInterface.getName());
	}

}
