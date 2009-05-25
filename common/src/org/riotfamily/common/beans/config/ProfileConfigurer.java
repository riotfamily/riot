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
 *   Alf Werder [alf dot werder at artundweise dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Configurer that uses a different set of properties (aka profile) depending 
 * on a system property. Purpose of this class is to allow the deployment of 
 * the same WAR file in different environments.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 */
public class ProfileConfigurer extends PlaceholderWithDefaultConfigurer
		implements InitializingBean {

	private String profileProperty = "profile";
	
	private String prefix = "profiles";
	
	private String defaultProfile = "_default";
	
	private String fileName = "application.properties";
	
	/**
	 * Sets the name of the system property that specifies the profile.
	 * Default value is <code>profile</code>.
	 */
	public void setProfileProperty(String profileProperty) {
		this.profileProperty = profileProperty;
	}

	/**
	 * Sets the path where the profiles are located.
	 * Default value is <code>profiles</code>. 
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Sets the name of the default profile.
	 * Default value is <code>_default</code>.
	 */
	public void setDefaultProfile(String defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	/**
	 * Sets the name of the properties file to be loaded.
	 * Default value is <code>application.properties</code>.
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void afterPropertiesSet() throws Exception {
		Resource defaultProps = new ClassPathResource(getPath(defaultProfile));
		Resource profileProps = new ClassPathResource(getPath(getProfile()));
		setLocations(new Resource[] { defaultProps, profileProps});
	}
	
	protected String getPath(String profile) {
		return prefix + "/" + profile + "/" + fileName;
	}

	protected String getProfile() {
		String profile = System.getProperty(profileProperty);
		if (profile == null) {
			try {
				profile = InetAddress.getLocalHost().getHostName();
			} 
			catch (UnknownHostException e) {
				throw new IllegalStateException(e);
			}
		}
		return profile;
	}
	
}
