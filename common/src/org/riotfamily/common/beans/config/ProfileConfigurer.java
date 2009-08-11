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
