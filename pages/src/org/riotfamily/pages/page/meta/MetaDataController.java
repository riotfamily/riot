package org.riotfamily.pages.page.meta;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.pages.mvc.cache.AbstractCachingPolicyController;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;
import org.springframework.web.servlet.ModelAndView;

public class MetaDataController extends AbstractCachingPolicyController {

	private Log log = LogFactory.getLog(MetaDataController.class);
			
	private String titlePrefix;
	
	private boolean appendQueryStringToCacheKey = false;
	
	public void setTitlePrefix(String titlePrefix) {
		this.titlePrefix = titlePrefix;
	}
	
	public void setAppendQueryStringToCacheKey(
			boolean appendQueryParamsToCacheKey) {
		this.appendQueryStringToCacheKey = appendQueryParamsToCacheKey;
	}
	
	public long getLastModified(HttpServletRequest request) {
        return PageUtils.getPageMap(request).getLastModified();
    }
	
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		Page page = PageUtils.getPage(request);
		if (page != null) {
			MetaData metaData = page.resolveMetaData(request);
			if (metaData != null) {
				renderMetaData(metaData, request, response);
			}
			else {
				log.warn("Page has no MetaData.");
			}
		}
		else {
			log.error("No Page found in request. MetaData will not be rendered.");
		}
		return null;
	}
	
	protected ModelAndView renderMetaData(MetaData metaData,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter writer = response.getWriter();
		TagWriter tag = new TagWriter(writer);
		
		tag.start(Html.TITLE).body();
		if (getTitlePrefix(request) != null) {
			tag.print(getTitlePrefix(request));
		}
		tag.print(metaData.getTitle());
		tag.end();
		
		if (metaData.getKeywords() != null) {
			writer.print("\n\t");
			tag.startEmpty(Html.META)
					.attribute(Html.META_NAME, "keywords")
					.attribute(Html.META_CONTENT, metaData.getKeywords())
					.end();
		}
		
		if (metaData.getDescription() != null) {
			writer.print("\n\t");
			tag.startEmpty(Html.META)
					.attribute(Html.META_NAME, "description")
					.attribute(Html.META_CONTENT, metaData.getDescription())
					.end();
		}
		
		return null;
	}
	
	protected String getTitlePrefix(HttpServletRequest request) {
		return titlePrefix;
	}
	
	protected void appendCacheKeyInternal(StringBuffer key, HttpServletRequest request) {		
		super.appendCacheKeyInternal(key, request);
		if (appendQueryStringToCacheKey) {
			key.append("query:").append(request.getQueryString());
		}
	}
	
}
