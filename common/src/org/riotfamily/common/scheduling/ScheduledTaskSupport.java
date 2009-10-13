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
package org.riotfamily.common.scheduling;

import org.springframework.core.Ordered;

/**
 * Abstract base class for scheduled tasks. Supports the setting of 
 * trigger names and the order via properties.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public abstract class ScheduledTaskSupport implements ScheduledTask, Ordered {

	private String[] triggerNames;
	
	private int order = Ordered.LOWEST_PRECEDENCE;
	
	public String[] getTriggerNames() {
		return triggerNames;
	}
	
	public void setTriggerNames(String[] triggerNames) {
		this.triggerNames = triggerNames;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

}
