package org.riotfamily.common.xml;

import org.springframework.context.ApplicationEvent;

/**
 * ApplicationEvent that is fired when a bean has been recofigured due to 
 * changes made to the configuration file.
 * 
 * @see org.riotfamily.common.xml.XmlBeanConfigurer#reconfigure()
 */
public class ConfigurationReloadedEvent extends ApplicationEvent {

	public ConfigurationReloadedEvent(Object source) {
		super(source);
	}

}
