package org.riotfamily.common.beans.injection;

/**
 * Interface to configure {@link ConfigurableBean} instances.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public interface BeanConfigurer {

	public void configure(ConfigurableBean bean);

}
