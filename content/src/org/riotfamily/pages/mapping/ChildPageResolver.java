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
package org.riotfamily.pages.mapping;

import java.util.Collection;
import java.util.Date;

import org.riotfamily.pages.config.VirtualPageType;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * Interface used by the {@link PageResolver} if the path is mapped to a 
 * {@link VirtualPageType}.
 */
public interface ChildPageResolver {

	public Page resolvePage(VirtualPageType type, Page parent, String pathComponent);
	
	public Collection<Page> listChildren(VirtualPageType type, Page parent);
	
	public Page getPage(VirtualPageType type, Site site, Object object);
	
	public Date getLastPublished(VirtualPageType type, Page parent, Object object);

}
