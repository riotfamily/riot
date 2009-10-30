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
package org.riotfamily.components.xstream;

import org.hibernate.proxy.HibernateProxy;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class HibernateProxyMapper extends MapperWrapper {

	public HibernateProxyMapper(Mapper wrapped) {
		super(wrapped);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String serializedClass(Class type) {
		if (type != null && HibernateProxy.class.isAssignableFrom(type)) {
			return type.getSuperclass().getName();
		}
		return super.serializedClass(type);
	}

}
