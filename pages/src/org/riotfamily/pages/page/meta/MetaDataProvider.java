package org.riotfamily.pages.page.meta;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.page.Page;

public interface MetaDataProvider {

	public MetaData getMetaData(Page page, HttpServletRequest request);

}
