package org.riotfamily.pages.model;

import java.util.Set;

import org.riotfamily.components.model.ContentContainer;

public class PageProperties extends ContentContainer {

	private Set<Page> pages;

	/*
	 * NOTE: PageProperties actually have a ont-to-one relation to a Page.
	 * It is mapped as a Set though, so that we can take advantage of 
	 * Hibernate's second level cache and lazy loading.
	 */
	public Page getPage() {
		return pages.iterator().next();
	}
	
}
