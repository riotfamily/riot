package org.riotfamily.pages.config;

import org.hibernate.Session;
import org.riotfamily.common.hibernate.TypedEntityListener;
import org.riotfamily.pages.model.Site;

public class SystemPageSyncListener extends TypedEntityListener<Site> {
	
	private SitemapSchema sitemapSchema;
	
	public SystemPageSyncListener(SitemapSchema sitemapSchema) {
		this.sitemapSchema = sitemapSchema;
	}

	@Override
	protected void entitySaved(Site site, Session session) throws Exception {
		sitemapSchema.syncSystemPages(site);
	}

}
