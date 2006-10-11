package org.riotfamily.pages.page.support;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.pages.page.PageDao;
import org.riotfamily.pages.page.PersistentPage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class SitemapSetupBean implements InitializingBean {

	private PageDao pageDao;
	
	private List pages;
	
	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public void setPages(List pages) {
		this.pages = pages;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(pageDao, "A PageDao must be set.");
		List rootPages = pageDao.listRootPages();
		if (rootPages == null || rootPages.isEmpty()) {
			savePages();
		}
	}
	
	protected void savePages() {
		if (pages != null) {
			int position = 0;
			Iterator it = pages.iterator();
			while (it.hasNext()) {
				PersistentPage page = (PersistentPage) it.next();
				page.setPosition(position++);
				pageDao.savePage(page);
			}
		}
	}
}
