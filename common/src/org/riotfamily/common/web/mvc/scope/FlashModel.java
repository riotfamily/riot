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
package org.riotfamily.common.web.mvc.scope;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.collection.SerializationSafeHashMap;

public class FlashModel extends SerializationSafeHashMap<String, Object>{

	private static final String REQUEST_ATTR = FlashModel.class.getName();
	
	public FlashModel() {
	}
	
	public FlashModel(Map<String, ?> model) {
		super(model);
	}

	void expose(HttpServletRequest request) {
		request.setAttribute(REQUEST_ATTR, this);
	}
	
	public static FlashModel get(HttpServletRequest request) {
		return (FlashModel) request.getAttribute(REQUEST_ATTR);
	}
	
}
