package org.riotfamily.pages.setup;

/**
 * Interface to be implemented by beans that need access to the WebsiteConfig.
 */
public interface WebsiteConfigAware {

	public void setWebsiteConfig(WebsiteConfig config);
}
