package org.riotfamily.pages.page.meta;

import javax.servlet.http.HttpServletRequest;

public interface MetaDataProvider {

	public MetaData getMetaData(HttpServletRequest request)
			throws Exception;
	
	public void appendCacheKey(StringBuffer key, HttpServletRequest request);
	
	public long getLastModified(HttpServletRequest request);

}
