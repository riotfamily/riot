/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package org.riotfamily.search.site;

import org.riotfamily.crawler.PageData;
import org.riotfamily.pages.model.Site;
import org.riotfamily.search.index.html.FieldExtractor;

/**
 * FieldExtractor that extracts the the language from the site's 
 * {@link Site#getLocale() locale}.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteLanguageExtractor implements FieldExtractor {

	private SiteIdentifier siteIdentifier;
	
	public SiteLanguageExtractor(SiteIdentifier siteIdentifier) {
		this.siteIdentifier = siteIdentifier;
	}

	public String getFieldValue(PageData pageData) {
		Site site = siteIdentifier.getSiteForUrl(pageData.getUrl());
		return site != null ? site.getLocale().getLanguage() : null;
	}
}