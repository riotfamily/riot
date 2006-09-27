package org.riotfamily.pages.page.support;

import org.springframework.context.ApplicationEvent;

/**
 * ApplicationEvent that is fired whenever the pageMap is modified.
 */
public class PageMappingEvent extends ApplicationEvent {

	public static final PageMappingEvent MAPPINGS_MODIFIED = 
			new PageMappingEvent();
	
	protected PageMappingEvent() {
		super(new Object());
	}

}
