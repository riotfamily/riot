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
package org.riotfamily.website.generic.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UrlPathHelper;

/**
 *  
 */
public class Pager {

	private int currentPage;

	private int pages;
	
	private long itemCount;

	private PagerItem firstPage;

	private PagerItem prevPage;

	private PagerItem[] prevPages;

	private PagerItem[] nextPages;

	private PagerItem nextPage;

	private PagerItem lastPage;

	private boolean gapToFirstPage;

	private boolean gapToLastPage;

	private String pageParam;

	private String linkPrefix;
	
	private String encodingScheme = "UTF-8";
	
	private boolean copyParameters = true;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	public Pager(int page, int pageSize, long itemCount) {
		currentPage = page;
		pages = (int) itemCount / pageSize + 1;
		if (itemCount % pageSize == 0) {
			pages--;
		}
		this.itemCount = itemCount;
	}
	
	public long getItemCount() {
		return itemCount;
	}

	public void setCopyParameters(boolean copyParameters) {
		this.copyParameters = copyParameters;
	}

	public void setEncodingScheme(String encodingScheme) {
		this.encodingScheme = encodingScheme;
	}

	public void initialize(HttpServletRequest request, int padding,
			String pageParam) {

		this.pageParam = pageParam;
		prepareLinkPrefix(request);

		int start = currentPage - padding;
		int end = currentPage + padding;

		if (start < 0) {
			end += -1 * start + 1;
		}
		if (end > pages) {
			start -= (end - pages);
		}
		if (start < 1) {
			start = 1;
		}
		if (end > pages) {
			end = pages;
		}

		gapToFirstPage = start > 2;
		gapToLastPage = end < pages - 1;

		int prevCount = Math.max(currentPage - start, 0);
		int nextCount = Math.max(end - currentPage, 0);

		if (start > 1) {
			firstPage = new PagerItem(linkPrefix, 1);
		}
		
		prevPages = new PagerItem[prevCount];
		for (int i = 0; i < prevCount; i++) {
			prevPages[i] = new PagerItem(linkPrefix, start + i);
		}
		if (prevCount > 0) {
			prevPage = prevPages[prevCount - 1];
		}
		
		nextPages = new PagerItem[nextCount];
		for (int i = 0; i < nextCount; i++) {
			nextPages[i] = new PagerItem(linkPrefix, currentPage + i + 1);
		}
		if (nextCount > 0) {
			nextPage = nextPages[0];
		}
		
		if (end < pages) {
			lastPage = new PagerItem(linkPrefix, pages);
		}
	}

	private void prepareLinkPrefix(HttpServletRequest request) {
		StringBuffer url = new StringBuffer(
				urlPathHelper.getOriginatingRequestUri(request));
		
		url.append('?');
		if (copyParameters) {
			String query = urlPathHelper.getOriginatingQueryString(request);
			if (query != null) {
				int i = query.indexOf(pageParam);
				if (i != -1) {
					url.append(query.substring(0, i));
				}
				else {
					url.append(query).append('&');
				}
			}
		}
		url.append(pageParam);
		url.append('=');
		linkPrefix = url.toString();
	}

	protected String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, encodingScheme);
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public int getPages() {
		return pages;
	}

	public PagerItem getFirstPage() {
		return firstPage;
	}

	public boolean isGapToFirstPage() {
		return gapToFirstPage;
	}

	public boolean isGapToLastPage() {
		return gapToLastPage;
	}

	public PagerItem getLastPage() {
		return lastPage;
	}

	public PagerItem[] getNextPages() {
		return nextPages;
	}

	public PagerItem[] getPrevPages() {
		return prevPages;
	}

	public PagerItem getNextPage() {
		return nextPage;
	}

	public PagerItem getPrevPage() {
		return prevPage;
	}
	
	public class PagerItem {

		private int number;

		private String link;
		
		public PagerItem(String linkPrefix, int number) {
			this.number = number;
			this.link = linkPrefix + number; 
		}
		
		public int getNumber() {
			return number;
		}
		
		public String getLink() {
			return link;
		}
	}
}