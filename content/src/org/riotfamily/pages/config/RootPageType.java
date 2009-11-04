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
package org.riotfamily.pages.config;

import java.util.Collections;
import java.util.List;

import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Site;

public class RootPageType extends SystemPageType {

	@Override
	public String getName() {
		return "root";
	}
	
	@Override
	public String getPathComponent() {
		return "";
	}
	
	@Override
	public List<String> getSuffixes() {
		return Collections.singletonList("/");
	}
	
	public void sync(Site site) {
		ContentPage page = site.getRootPage();
		if (page == null) {
			page = createPage(site, null);
			site.setRootPage(page);
		}
		update(page);
	}
}
