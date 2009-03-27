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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.setup.config;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.riotfamily.components.model.Content;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SiteDefinition {

	private String name;
	
	private String hostName;
	
	private String pathPrefix;

	private Locale locale;
	
	private boolean enabled = true;

	private Map<String, Object> properties;
	
	private List<SiteDefinition> siteDefinitions;

	public void setName(String name) {
		this.name = name;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public void setSiteDefinitions(List<SiteDefinition> sites) {
		this.siteDefinitions = sites;
	}
	
	public void createSites(List<Site> list, Site masterSite) {
		Site site = new Site();
		site.setName(name);
		site.setHostName(hostName);
		site.setPathPrefix(pathPrefix);
		site.setLocale(locale);
		site.setEnabled(enabled);
		site.setMasterSite(masterSite);
		if (properties != null) {
			Content props = new Content();
			props.wrap(properties);
			site.setProperties(props);
		}
		site.save();
		list.add(site);
		if (siteDefinitions != null) {
			for (SiteDefinition definition : siteDefinitions) {
				definition.createSites(list, site);
			}
		}
	}
}
