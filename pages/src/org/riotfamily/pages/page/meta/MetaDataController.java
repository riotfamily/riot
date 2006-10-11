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
import org.springframework.web.servlet.ModelAndView;

public class MetaDataController extends AbstractCachingPolicyController {

	public static final String DEFAULT_TITLE_DELIMITER = " - ";
	
	private Log log = LogFactory.getLog(MetaDataController.class);
			
	private String titlePrefix;
	
	private String titleDelimiter = DEFAULT_TITLE_DELIMITER;
	
	private MetaDataProvider metaDataProvider;
	
	public void setTitlePrefix(String titlePrefix) {
		this.titlePrefix = titlePrefix;
	}
	
	protected String getTitlePrefix(HttpServletRequest request) {
		return titlePrefix;
	}
		
	public void setTitleDelimiter(String titleDelimiter) {
		this.titleDelimiter = titleDelimiter;
	}
	
	public void setMetaDataProvider(MetaDataProvider metaDataProvider) {
		this.metaDataProvider = metaDataProvider;
	}

	public long getLastModified(HttpServletRequest request) {
        return metaDataProvider.getLastModified(request);
    }
		
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) {

		try {
			MetaData metaData  = metaDataProvider.getMetaData(request);
			if (metaData != null) {
				renderMetaData(metaData, request, response);
			}
			else {
				log.error("No MetaData - page title will not be rendered.");
			}
		}
		catch (Exception e) {
			log.error("Error rendering MetaData", e);
		}
		return null;
	}
	
	protected ModelAndView renderMetaData(MetaData metaData, 
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		PrintWriter writer = response.getWriter();
		TagWriter tag = new TagWriter(writer);
		
		tag.start(Html.TITLE).body();
		if (getTitlePrefix(request) != null) {
			tag.print(getTitlePrefix(request));
			if (metaData.getTitle() != null) {
				tag.print(titleDelimiter);
			}
		}
		if (metaData.getTitle() != null) {
			tag.print(metaData.getTitle());
		}
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
	
}
