package org.riotfamily.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ContentChangedEvent extends ApplicationEvent {

	private String url;

	public ContentChangedEvent(Object source, String url) {
		super(source);
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}
	
}
