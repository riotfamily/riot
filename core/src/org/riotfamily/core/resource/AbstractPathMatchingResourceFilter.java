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
package org.riotfamily.core.resource;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public abstract class AbstractPathMatchingResourceFilter 
		implements ResourceFilter {

	private String[] matches;
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	public void setMatch(String match) {
		this.matches = new String[] { match };
	}
	
	public void setMatches(String[] matches) {
		this.matches = matches;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
	
	public boolean matches(String path) {
		for (int i = 0; i < matches.length; i++) {
			if (pathMatcher.match(matches[i], path)) {
				return true;
			}
		}
		return false;
	}

}
