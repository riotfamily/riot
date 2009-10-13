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
package org.riotfamily.common.beans.factory;

import java.util.List;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * FactoryBean that looks for all beans of a certain type and returns the one
 * with the {@link Ordered#HIGHEST_PRECEDENCE highest precedence}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class HighestPrecedenceFactoryBean implements FactoryBean, 
		ApplicationContextAware {

	private Class<? extends Ordered> type;
	
	private ApplicationContext applicationContext;
	
	public void setType(Class<? extends Ordered> type) {
		Assert.isAssignable(Ordered.class, type, "Type must implement the " +
				"'org.springframework.core.Ordered' interface.");
		
		this.type = type;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Object getObject() throws Exception {
		Assert.notNull(type, "A type must be specified.");
		List<?> beans = SpringUtils.orderedBeansIncludingAncestors(applicationContext, type);
		Assert.notEmpty(beans, "At last one bean of type '" + type.getName()
				+ "' must be present.");
		
		return beans.get(0);
	}

	public Class<? extends Ordered> getObjectType() {
		return type;
	}

	public boolean isSingleton() {
		return true;
	}

}
