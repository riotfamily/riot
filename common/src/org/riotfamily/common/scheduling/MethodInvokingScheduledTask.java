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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.util.MethodInvoker;

public class MethodInvokingScheduledTask extends MethodInvoker 
		implements ScheduledTask, Ordered, InitializingBean {

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
	
	public void afterPropertiesSet() throws Exception {
		prepare();
	}
	
	public void execute() throws Exception {
		invoke();
	}
}
