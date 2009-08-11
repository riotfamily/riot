package org.riotfamily.pages.config;

import java.util.Collections;
import java.util.List;

import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

public class RootPage extends SystemPage {

	@Override
	public String getName() {
		return "root";
	}
	
	@Override
	public String getPathComponent() {
		return "";
	}
	
	@Override
	public List<String> getSuffixes() {
		return Collections.singletonList("");
	}
	
	public void sync(Site site) {
		Page page = site.getRootPage();
		if (page == null) {
			page = createPage(site, null);
			site.setRootPage(page);
		}
		update(page);
	}
}
