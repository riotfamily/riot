package org.riotfamily.website.freemarker;

import java.io.File;
import java.io.IOException;

import org.riotfamily.common.freemarker.RiotFreeMarkerConfigurer;
import org.riotfamily.common.util.RiotLog;
import org.springframework.core.io.Resource;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import freemarker.cache.TemplateLoader;

/**
 * FreeMarkerConfigurer that uses a {@link RiotFileTemplateLoader} so that
 * the Cachius cache knows which templates are involved in the creation of a
 * CacheItem. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class WebsiteFreeMarkerConfigurer extends RiotFreeMarkerConfigurer {

	private RiotLog log = RiotLog.get(this);
	
	/**
	 * {@inheritDoc}
	 * This class overrides the super method to create a 
	 * {@link RiotFileTemplateLoader} instead of the regular FileTemlateLoader.
	 */
	protected TemplateLoader getTemplateLoaderForPath(String templateLoaderPath) {
		if (isPreferFileSystemAccess()) {
			// Try to load via the file system, fall back to SpringTemplateLoader
			// (for hot detection of template changes, if possible).
			try {
				Resource path = getResourceLoader().getResource(templateLoaderPath);
				File file = path.getFile();  // will fail if not resolvable in the file system
				if (log.isDebugEnabled()) {
					log.debug(
							"Template loader path [" + path + "] resolved to file path [" + file.getAbsolutePath() + "]");
				}
				return new RiotFileTemplateLoader(file);
			}
			catch (IOException ex) {
				if (log.isDebugEnabled()) {
					log.debug("Cannot resolve template loader path [" + templateLoaderPath +
							"] to [java.io.File]: using SpringTemplateLoader as fallback", ex);
				}
				return new SpringTemplateLoader(getResourceLoader(), templateLoaderPath);
			}
		}
		else {
			// Always load via SpringTemplateLoader (without hot detection of template changes).
			log.debug("File system access not preferred: using SpringTemplateLoader");
			return new SpringTemplateLoader(getResourceLoader(), templateLoaderPath);
		}
	}
}
