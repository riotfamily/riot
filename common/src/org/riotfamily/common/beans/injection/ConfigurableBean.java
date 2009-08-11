package org.riotfamily.common.beans.injection;

/**
 * Base class for configurable domain objects. Can be used as lightweight 
 * alternative for Spring's <code>@Configurable</code> annotation.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public abstract class ConfigurableBean {

	static BeanConfigurer configurer;

	public ConfigurableBean() {
		if (configurer != null) {
			configurer.configure(this);
		}
	}
	
}
