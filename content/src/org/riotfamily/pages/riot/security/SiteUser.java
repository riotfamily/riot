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
package org.riotfamily.pages.riot.security;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.riotfamily.core.security.auth.User;
import org.riotfamily.pages.model.Site;

@Entity
public class SiteUser extends User {
	
	private Set<Site> sites;

	@ManyToMany
	@LazyCollection(LazyCollectionOption.FALSE)
	public Set<Site> getSites() {
		return sites;
	}
	
	public void setSites(Set<Site> sites) {
		this.sites = sites;
	}
	
}
