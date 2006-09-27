package org.riotfamily.common.web.dwr;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.directwebremoting.AccessControl;
import org.directwebremoting.Configurator;
import org.directwebremoting.Container;
import org.directwebremoting.ConverterManager;
import org.directwebremoting.Creator;
import org.directwebremoting.CreatorManager;
import org.directwebremoting.spring.BeanCreator;
import org.springframework.beans.factory.BeanCreationException;

public class SpringConfigurator implements Configurator {

	private Map serviceBeans;
	
	private Class[] serviceInterfaces;
	
	private Properties converterTypes;
	
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
				catch (Exception e) {
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
