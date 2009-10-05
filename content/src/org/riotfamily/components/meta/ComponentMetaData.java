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
package org.riotfamily.components.meta;

import java.util.Map;

import net.sf.json.JSONObject;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.common.util.FormatUtils;

@DataTransferObject
public class ComponentMetaData {

	private String type;
	
	private String name;
	
	private String icon;
	
	private String form;
	
	private Map<String, Object> defaults;

	@SuppressWarnings("unchecked")
	public ComponentMetaData(String type, Map<String, String> data) {
		this.type = type;
		if (data != null) {
			this.name = data.get("name");
			this.icon = data.get("icon");
			this.form = data.get("form");
			JSONObject json = JSONObject.fromObject(data.get("defaults"));
			if (!json.isNullObject()) {
				this.defaults = json;
			}
		}
		if (this.name == null) {
			this.name = FormatUtils.xmlToTitleCase(type);
		}
	}

	@RemoteProperty
	public String getType() {
		return type;
	}
	
	@RemoteProperty
	public String getName() {
		return name;
	}

	@RemoteProperty
	public String getIcon() {
		return icon;
	}

	public String getForm() {
		return form;
	}

	public Map<String, Object> getDefaults() {
		return defaults;
	}
	
}
