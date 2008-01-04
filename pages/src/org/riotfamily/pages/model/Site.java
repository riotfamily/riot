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
	
	private boolean enabled = true;

	private long position;
	
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
	
	public boolean matches(String hostName, String path) {
		return (this.hostName == null || this.hostName.equals(hostName))
				&& (pathPrefix == null || path.startsWith(pathPrefix + "/"));
	}

	public Site getMasterSite() {
		return this.masterSite;
	}

	public void setMasterSite(Site masterSite) {
		this.masterSite = masterSite;
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
