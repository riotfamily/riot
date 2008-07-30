package org.riotfamily.pages.riot.security;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.hibernate.security.User;

@Entity
public class SiteUser extends User {
	
	private Set<Site> sites;

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	public Set<Site> getSites() {
		return sites;
	}
	
	public void setSites(Set<Site> sites) {
		this.sites = sites;
	}
	
}
