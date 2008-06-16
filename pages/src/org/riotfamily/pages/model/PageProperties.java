package org.riotfamily.pages.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.riotfamily.components.model.ContentContainer;

@Entity
@Table(name="riot_page_properties")
public class PageProperties extends ContentContainer {

	private Set<Page> pages;

	@OneToMany
	@JoinColumn(name="pageProperties")
	public Set<Page> getPages() {
		return pages;
	}

	public void setPages(Set<Page> pages) {
		this.pages = pages;
	}

	/*
	 * NOTE: PageProperties actually have a ont-to-one relation to a Page.
	 * It is mapped as a Set though, so that we can take advantage of 
	 * Hibernate's second level cache and lazy loading.
	 */
	@Transient
	public Page getPage() {
		return pages.iterator().next();
	}
	
}
