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
package org.riotfamily.core.screen.list;

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * State of a list screen. Instances are stored in the HTTP session.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ListState implements Serializable {

	private String key;
	
	private String screenId;
	
	private Locale locale;
	
	private String parentId;
	
	private ListParamsImpl params;
	
	private ChooserSettings chooserSettings;
	
	public static ListState get(HttpServletRequest request, String key) {
		if (key.startsWith("list:")) {
			return (ListState) request.getSession().getAttribute(key);
		}
		return new ListState(key, request);
	}
	
	public static void put(HttpServletRequest request, String key, 
			ListState state) {
		
		request.getSession().setAttribute(key, state);
	}
	
	public ListState(String screenId, HttpServletRequest request) {
		this.screenId = screenId;
		this.locale = RequestContextUtils.getLocale(request);
		this.chooserSettings = new ChooserSettings(request);
	}
	
	public ListState(String key, String screenId, Locale locale,
			String parentId, int pageSize, ChooserSettings chooserSettings) {
		
		this.key = key;
		this.screenId = screenId;
		this.locale = locale;
		this.parentId = parentId;
		this.chooserSettings = chooserSettings;
		this.params = new ListParamsImpl(pageSize);
	}

	public String getKey() {
		return key;
	}

	public String getScreenId() {
		return screenId;
	}

	public String getParentId() {
		return parentId;
	}

	public Locale getLocale() {
		return locale;
	}

	public ListParamsImpl getParams() {
		return params;
	}
	
	public ChooserSettings getChooserSettings() {
		return chooserSettings;
	}

}
