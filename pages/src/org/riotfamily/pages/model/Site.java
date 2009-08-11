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
package org.riotfamily.pages.model;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CollectionOfElements;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.components.model.Content;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_sites")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
public class Site extends ActiveRecordBeanSupport {

	private String name;
	
	private String hostName;
	
	private String pathPrefix;

	private Locale locale;
	
	private Site masterSite;
	
	private Set<Site> derivedSites;
	
	private boolean enabled = true;

	private long position;
	
	private Set<String> aliases;

	private Page rootPage;
	
	private Content properties;
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		if (name == null) {
			StringBuffer sb = new StringBuffer();
			if (hostName != null) {
				sb.append(hostName);
				if (pathPrefix != null) {
					sb.append(pathPrefix);	
				}
			}
			else {
				sb.append(locale);
			}
			name = sb.toString();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getHostName() {
		return this.hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPathPrefix() {
		return this.pathPrefix;
	}

	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = normalizePrefix(pathPrefix);
	}
	
	private String normalizePrefix(String prefix) {
		if (prefix != null) {
			// Strip trailing slash
			if (prefix.endsWith("/")) {
				prefix = prefix.substring(0, prefix.length() - 1);
			}
			// represent empty prefixes to null
			if (!StringUtils.hasText(prefix)) {
				return null;
			}
			// Add leading slash
			if (!prefix.startsWith("/")) {
				prefix = "/" + prefix;
			}
		}
		return prefix;
	}
	
	public String stripPrefix(String path) {
		if (pathPrefix != null && path != null && path.startsWith(pathPrefix)) {
			return path.substring(pathPrefix.length());
		}
		return path;
	}
	
	/**
	 * Returns whether the given hostName matches the configured one.
	 * Sites without a hostName will match any host.
	 */
	public boolean hostNameMatches(String hostName) {
		return hostNameMatches(hostName, true);
	}
	
	/**
	 * Returns whether the given hostName matches the configured one.
	 * @param hostName The hostName to match
	 * @param greedy Whether a <code>null</code> hostName should match any host
	 * @return <code>true</code> if the hostName matches, <code>false</code> otherwise
	 */
	public boolean hostNameMatches(String hostName, boolean greedy) {
		return (this.hostName == null && greedy) 
				|| (this.hostName != null && this.hostName.equals(hostName))
				|| (this.aliases != null && this.aliases.contains(hostName));
	}
	
	public boolean prefixMatches(String path) {
		return pathPrefix == null || path.startsWith(pathPrefix + "/") || pathPrefix.equals(path);
	}
			
	public boolean matches(String hostName, String path) {
			return hostNameMatches(hostName) && prefixMatches(path);
	}

	@ManyToOne(cascade=CascadeType.MERGE)
	public Site getMasterSite() {
		return this.masterSite;
	}

	public void setMasterSite(Site masterSite) {
		this.masterSite = masterSite;
	}
		
	@OneToMany(mappedBy="masterSite")
	public Set<Site> getDerivedSites() {
		return this.derivedSites;
	}

	public void setDerivedSites(Set<Site> derivedSites) {
		this.derivedSites = derivedSites;
	}

	@Column(name="pos")
	public long getPosition() {
		if (position == 0) {
			position = System.currentTimeMillis();
		}
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}
	
	@CollectionOfElements
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	public Set<String> getAliases() {
		return this.aliases;
	}

	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	public Page getRootPage() {
		return rootPage;
	}

	public void setRootPage(Page rootPage) {
		this.rootPage = rootPage;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	public Content getProperties() {
		if (properties == null) {
			properties = new Content();
		}
		return properties;
	}

	public void setProperties(Content properties) {
		this.properties = properties;
	}

	public Object getProperty(String key) {
		Object value = getProperties().get(key);
		if (value == null && masterSite != null) {
			value = masterSite.getProperty(key);
		}
		return value;
	}
		
	@Transient
	public Map<String, Object> getPropertiesMap() {
		Map<String, Object> mergedProperties;
		if (masterSite != null) {
			mergedProperties = masterSite.getPropertiesMap();
		}
		else {
			mergedProperties = new HashMap<String, Object>();
		}
		mergedProperties.putAll(getProperties());
		return mergedProperties;
	}
	
	/**
	 */
	public String makeAbsolute(boolean secure, String defaultHost, 
			String contextPath, String path) {
		
		StringBuffer url = new StringBuffer();
		
		if (hostName != null || defaultHost != null) {
			url.append(secure
					? ServletUtils.SCHEME_HTTPS 
					: ServletUtils.SCHEME_HTTP);
			
	        url.append("://");
	        if (hostName != null) {
				url.append(hostName);
			}
			else {
				url.append(defaultHost);
			}
		}
        
        if (contextPath.length() > 0 && !path.startsWith(contextPath)) {
        	url.append(contextPath);
        }
        if (pathPrefix != null && !path.startsWith(contextPath + pathPrefix)) {
        	url.append(pathPrefix);
        }
		url.append(path);
		return url.toString();
	}
		
	// ----------------------------------------------------------------------
	// Object identity methods
	// ----------------------------------------------------------------------

	public String toString() {
		return getName();
	}
	
	public int hashCode() {
		return getName().hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Site)) {
			return false;
		}
		Site other = (Site) obj;
		
		return ObjectUtils.nullSafeEquals(this.hostName, other.getHostName())
				&& ObjectUtils.nullSafeEquals(this.pathPrefix, other.getPathPrefix())
				&& ObjectUtils.nullSafeEquals(this.locale, other.getLocale())
				&& ObjectUtils.nullSafeEquals(this.masterSite, other.getMasterSite());
	}
	
	// ----------------------------------------------------------------------
	// ActiveRecord methods
	// ----------------------------------------------------------------------
	
	public void refreshIfDetached() {
		Session session = getSession();
		if (!session.contains(this)) {
			session.refresh(this);
		}
	}
	
	public static Site load(Long id) {
		return load(Site.class, id);
	}
	
	public static Site loadDefaultSite() {
		return (Site) getSession().createCriteria(Site.class)
				.setCacheable(true)
				.setCacheRegion("pages")
				.setMaxResults(1)
				.uniqueResult();
	}
	
	public static Site loadByLocale(Locale locale) {
		return load("from Site where locale = ?", locale);
	}
	
	public static List<Site> findAll() {
		return find("from Site order by position");
	}
	
	public static Site loadByHostNameAndPath(String hostName, String path) {
		for (Site site : findAll()) {
			if (site.matches(hostName, path)) {
				return site;
			}
		}
		return null;
	}
	
	
}
