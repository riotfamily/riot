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
package org.riotfamily.common.web.resource;

import java.io.FilterReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that allows to filter resources that are served by a 
 * ResourceController.
 * 
 * @see org.riotfamily.common.web.resource.ResourceController#setFilters
 */
public interface ResourceFilter {

	/**
	 * Returns whether the filter should be applied to the resource denoted
	 * by the given path.
	 */
	public boolean matches(String path);
	
	/**
	 * Returns a FilterReader that does the actual filtering.
	 */
	public FilterReader createFilterReader(Reader in, HttpServletRequest request);

}
