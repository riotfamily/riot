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
package org.riotfamily.pages.riot.form;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.riotfamily.common.collection.ToStringComparator;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.pages.model.Site;

public class SiteLocalesOptionsModel implements OptionsModel {

	public Collection<?> getOptionValues(Element element) {
		Set<Locale> locales = new TreeSet<Locale>(new ToStringComparator());
		for (Site site : Site.findAll()) {
			locales.add(site.getLocale());
		}
		return locales;
	}

}
