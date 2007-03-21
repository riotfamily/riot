package org.riotfamily.pages.dao;

import java.util.Collection;
import java.util.Locale;

import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PathAndLocale;


public interface PageDao {

	public Page loadPage(Long id);
	
	public Page findPage(PathAndLocale location);

	public PageAlias findPageAlias(PathAndLocale location);
	
	public void savePage(Page parent, Page child);

	public void saveRootPage(Page page, Locale locale);

	public Page addTranslation(Page page, Locale locale);

	public void updatePage(Page page);

	public void deletePage(Page page);

	public Collection listRootPages(Locale locale);

}