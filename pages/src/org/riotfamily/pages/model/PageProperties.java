package org.riotfamily.pages.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ContentContainer;

@Entity
@Table(name="riot_page_properties")
public class PageProperties extends ContentContainer {

	private Set<Page> pages;

	
	public PageProperties() {
	}
	
	public PageProperties(Page page) {
		pages = Collections.singleton(page);
		Page master = page.getMasterPage();
		if (master != null) {
			Map<String, Object> masterProps = master.getPageProperties().unwrap(true);
			for (Entry<String, Object> entry : masterProps.entrySet()) {
				if (entry.getValue() instanceof ComponentList) {
					ComponentList list = (ComponentList) entry.getValue();
					getPreviewVersion().setValue(entry.getKey(), list.createCopy());
				}
			}
		}
	}

	@OneToMany(mappedBy="pageProperties") //, fetch=FetchType.EAGER
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
	@Cascade({CascadeType.MERGE, CascadeType.SAVE_UPDATE})
	public Set<Page> getPages() {
		return pages;
	}

	public void setPages(Set<Page> pages) {
		this.pages = pages;
	}

	/*
	 * NOTE: PageProperties actually have a one-to-one relation to a Page.
	 * It is mapped as a Set though, so that we can take advantage of 
	 * Hibernate's second level cache and lazy loading.
	 */
	@Transient
	public Page getPage() {
		return pages.iterator().next();
	}
	
	public Map<String, Object> unwrap() {
		return unwrap(false);
	}
	
	public Map<String, Object> unwrap(boolean preview) {
		Map<String, Object> mergedProperties;
		Page masterPage = getPage().getMasterPage();
		if (masterPage != null) {
			mergedProperties = masterPage.getPageProperties().unwrap(preview);
		}
		else {
			mergedProperties = new HashMap<String, Object>();
		}
		mergedProperties.putAll(unwrapLocal(preview));
		return mergedProperties;
	}
	
	public Map<String, Object> unwrapLocal(boolean preview) {
		return super.unwrap(preview);
	}
	
}
