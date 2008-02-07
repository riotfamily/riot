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

import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Site {

	private Long id;

	private String name;
	
	private String hostName;
	
	private String pathPrefix;

	private Locale locale;
	
	private String theme;
	
	private Site masterSite;
	
	private Set derivedSites;
	
	private boolean enabled = true;

	private long position;
	
	private Set aliases;
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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
				if (theme != null) {
					sb.append(" (").append(theme).append(')');
				}
			}
			name = sb.toString();
		}
		return name;
	}

	public void setName(String serverName) {
		this.name = serverName;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
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
		if (pathPrefix != null) {
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
		return pathPrefix == null || path.startsWith(pathPrefix + "/");
	}
			
	public boolean matches(String hostName, String path) {
			return hostNameMatches(hostName) && prefixMatches(path);
	}

	public Site getMasterSite() {
		return this.masterSite;
	}

	public void setMasterSite(Site masterSite) {
		this.masterSite = masterSite;
	}
	
	public Set getDerivedSites() {
		return this.derivedSites;
	}

	public void setDerivedSites(Set derivedSites) {
		this.derivedSites = derivedSites;
	}

	public long getPosition() {
		if (position == 0) {
			position = System.currentTimeMillis();
		}
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}
	
	public Set getAliases() {
		return this.aliases;
	}

	public void setAliases(Set aliases) {
		this.aliases = aliases;
	}

	public StringBuffer getAbsoluteUrl(boolean secure) {
		Assert.state(hostName != null, "Can't build an absolute URL because " +
				"no hostName is set for the Site " + getName());
		
		StringBuffer url = new StringBuffer();
		url.append(secure
				? ServletUtils.SCHEME_HTTPS 
				: ServletUtils.SCHEME_HTTP);
		
        url.append("://");
		url.append(hostName);
		if (pathPrefix != null) {
			url.append(pathPrefix);
		}
		return url;
	}
	
	/**
	 * Returns an absolute URL for the Site. It works exactly like
	 * {@link #getAbsoluteSiteUrl(Site, HttpServletRequest, boolean)} and
	 * determines the secure flag from the passed in request.
	 * 
	 * @param request the current request
	 * @return an url for the given site 
	 */
	public StringBuffer getAbsoluteUrl(HttpServletRequest request) {
		return getAbsoluteUrl(request, request.isSecure());
	}
	
	/**
	 * Returns an absolute URL for the Site. If the site does not have a host
	 * name configured the server name of the given request will be used.
	 * 
	 * @param request the current request
	 * @param secure whether to use the https scheme
	 * @return an url for the given site 
	 */
	public StringBuffer getAbsoluteUrl(HttpServletRequest request, boolean secure) {
		StringBuffer url = new StringBuffer();
		url.append(secure
				? ServletUtils.SCHEME_HTTPS 
				: ServletUtils.SCHEME_HTTP);
		
        url.append("://");
        if (hostName != null) {
			url.append(hostName);
		}
		else {
			url.append(request.getServerName());
	        int port = request.getServerPort();
	        if (port <= 0) {
	            port = 80;
	        }
	        // Append the port unless it's the protocol's default 
	        if ((!secure && port != 80) || (secure && port != 443)) {
	        	// If the protocol changes, we don't know the port and need to assume the default 
	        	if (secure == request.isSecure()) {
		            url.append(':');
		            url.append(port);
		        }
	        }
		}
		url.append(request.getContextPath());
		if (pathPrefix != null) {
			url.append(pathPrefix);
		}
		return url;
	}

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
		
		return ObjectUtils.nullSafeEquals(this.name, other.name)
				&& ObjectUtils.nullSafeEquals(this.hostName, other.hostName)
				&& ObjectUtils.nullSafeEquals(this.pathPrefix, other.pathPrefix)
				&& ObjectUtils.nullSafeEquals(this.locale, other.locale)
				&& ObjectUtils.nullSafeEquals(this.theme, other.theme)
				&& ObjectUtils.nullSafeEquals(this.masterSite, other.masterSite);
	}
}
