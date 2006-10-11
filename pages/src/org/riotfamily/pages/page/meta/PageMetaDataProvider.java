package org.riotfamily.pages.page.meta;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;

public class PageMetaDataProvider implements MetaDataProvider {

	public MetaData getMetaData(HttpServletRequest request) 
			throws Exception {
		
		Page page = PageUtils.getPage(request);
		return new MetaData(page.getTitle(), page.getKeywords(), 
				page.getDescription());
	}
	
	public long getLastModified(HttpServletRequest request) {
        return PageUtils.getPageMap(request).getLastModified();
    }
	
	public void appendCacheKey(StringBuffer key, HttpServletRequest request) {
	}

}
