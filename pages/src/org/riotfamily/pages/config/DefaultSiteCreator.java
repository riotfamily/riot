package org.riotfamily.pages.config;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.AbstractSetupBean;
import org.riotfamily.pages.model.Site;

public class DefaultSiteCreator extends AbstractSetupBean {

	public DefaultSiteCreator(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	protected void setup(Session session) throws Exception {
		Site site  = Site.loadDefaultSite();
		if (site == null) {
			site = new Site();
			site.setLocale(Locale.getDefault());
			site.setName("default");
			site.save();
		}
	}
	
}
