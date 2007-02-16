/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

public class MetaDataController extends AbstractCachingPolicyController 
		implements MessageSourceAware {

	public static final String DEFAULT_TITLE_DELIMITER = " - ";
	
	private Log log = LogFactory.getLog(MetaDataController.class);
			
	private String titlePrefix;
	
	private String titlePrefixMessageKey;
	
	private String titleDelimiter = DEFAULT_TITLE_DELIMITER;
	
	private MessageSource messageSource;
	
	private MetaDataProvider metaDataProvider;
	
	public void setTitlePrefix(String titlePrefix) {
		this.titlePrefix = titlePrefix;
	}
	
	public void setTitlePrefixMessageKey(String titlePrefixMessageKey) {
		this.titlePrefixMessageKey = titlePrefixMessageKey;
	}	
	
	public void setTitleDelimiter(String titleDelimiter) {
		this.titleDelimiter = titleDelimiter;
	}
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;		
	}
	
	public void setMetaDataProvider(MetaDataProvider metaDataProvider) {
		this.metaDataProvider = metaDataProvider;
	}

	public long getLastModified(HttpServletRequest request) {
        return metaDataProvider.getLastModified(request);
    }
	
	protected String getTitlePrefix(HttpServletRequest request) {
		if (titlePrefixMessageKey != null) {
			return messageSource.getMessage(titlePrefixMessageKey, null, titlePrefix, 
						RequestContextUtils.getLocale(request));
		}
		else {
			return titlePrefix;
		}
	}
		
		
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) {

		try {
			MetaData metaData = metaDataProvider.getMetaData(request);
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
