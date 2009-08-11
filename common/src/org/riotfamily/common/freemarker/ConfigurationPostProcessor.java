package org.riotfamily.common.freemarker;

import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Interface used by the {@link RiotFreeMarkerConfigurer} to support modular
 * post processing of the FreeMarker {@link Configuration}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public interface ConfigurationPostProcessor {

	public void postProcessConfiguration(Configuration config) 
			throws IOException, TemplateException;

}
