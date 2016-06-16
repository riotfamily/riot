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
package org.riotfamily.core.form.options;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.options.OptionsModel;

public class HqlOptionsModel implements OptionsModel {

	private SessionFactory sessionFactory;
	
	private String hql;
	
	public HqlOptionsModel(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setHql(String hql) {
		this.hql = hql;
	}

	public Collection<?> getOptionValues(Element element) {
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

}
