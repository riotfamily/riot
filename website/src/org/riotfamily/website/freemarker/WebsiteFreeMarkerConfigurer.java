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
package org.riotfamily.website.freemarker;

import java.io.File;
import java.io.IOException;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.common.web.view.freemarker.RiotFreeMarkerConfigurer;
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
