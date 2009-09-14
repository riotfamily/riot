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
package org.riotfamily.cachius.http.header;

import java.io.Serializable;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class Cookies implements Serializable {

	private static final long serialVersionUID = -6620847178807123964L;

	private ArrayList<AbstractCookie> cookies;
	
	public void add(AbstractCookie cookie) {
		if (cookies == null) {
			cookies = new ArrayList<AbstractCookie>();
		}
		cookies.add(cookie);
	}
	
	public void clear() {
		cookies = null;
	}
	
	public void send(HttpServletRequest request, 
			HttpServletResponse response) {

		if (cookies != null) {
			for (AbstractCookie cookie : cookies) {
				cookie.send(request, response);
			}
		}
	}
}
