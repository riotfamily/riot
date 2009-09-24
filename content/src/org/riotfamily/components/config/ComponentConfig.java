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
package org.riotfamily.components.config;

import java.util.Map;

import org.riotfamily.common.beans.property.PropertyUtils;

public class ComponentConfig {

	private String type;
	
	private int min;
	
	private Integer max;
	
	
	@SuppressWarnings("unchecked")
	public ComponentConfig(Object obj) {
		if (obj instanceof String) {
			type = (String) obj;
		}
		else if (obj instanceof Map) {
			Map map = (Map) obj;
			PropertyUtils.setProperties(this, map);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

}
