package org.riotfamily.pages.riot.security;

import java.util.Set;

import org.riotfamily.riot.hibernate.security.User;

public class SiteUser extends User {
	
	private Set sites;

	public Set getSites() {
		return sites;
	}

	public void setSites(Set sites) {
		this.sites = sites;
	}
	
}
