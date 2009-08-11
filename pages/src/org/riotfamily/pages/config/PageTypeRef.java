package org.riotfamily.pages.config;

import org.springframework.util.Assert;

public class PageTypeRef extends PageType {

	@Override
	void register(SitemapSchema schema) {
		PageType ref = schema.getPageType(getName());
		Assert.notNull(ref, "Referenced type not found: " + getName());
		copyFrom(ref);
	}
	
	private void copyFrom(PageType ref) {
		setChildTypes(ref.getChildTypes());
		setHandler(ref.getHandler());
		setSuffixes(ref.getSuffixes());
	}
	
}
