/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.components.model.Content;
import org.springframework.util.ObjectUtils;

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
	
	private Locale locale;
	
	private Site masterSite;
	
	private Set<Site> derivedSites;
	
	private boolean enabled = true;

	private long position;
	
	private Set<String> aliases;

	private ContentPage rootPage;
	
	private Content properties;
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		if (name == null) {
			if (hostName != null) {
				name = hostName;
			}
			else {
				name = locale.toString();
			}
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
	public ContentPage getRootPage() {
		return rootPage;
	}

	public void setRootPage(ContentPage rootPage) {
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
		url.append(path);
		return url.toString();
	}
		
	// ----------------------------------------------------------------------
	// Object identity methods
	// ----------------------------------------------------------------------

	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Site)) {
			return false;
		}
		Site other = (Site) obj;
		
		return ObjectUtils.nullSafeEquals(this.hostName, other.getHostName())
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
	
	public static Site loadByHostName(String hostName) {
		for (Site site : findAll()) {
			if (site.hostNameMatches(hostName)) {
				return site;
			}
		}
		return null;
	}
	
	
}
