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

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.forms.element.suggest.AutocompleteTextField;
import org.riotfamily.forms.element.suggest.AutocompleterModel;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DistinctAutoCompleterModel extends HibernateDaoSupport 
		implements AutocompleterModel {

	private boolean caseSensitive = false;
	
	private String parentProperty;
	
	public DistinctAutoCompleterModel(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getSuggestions(String search,
			AutocompleteTextField element, HttpServletRequest request) {
		
		String property = element.getEditorBinding().getProperty();
		Class<?> clazz = element.getEditorBinding().getBeanClass();
		
		StringBuilder hql = new StringBuilder("select distinct ")
				.append(property).append(" from ").append(clazz.getName())
				.append(" where ");
		
		if (caseSensitive) {
			hql.append(property);
		}
		else {
			hql.append("lower(").append(property).append(')');
			search = search.toLowerCase();
		}
		
		if (parentProperty != null) {
			hql.append(" and ").append(parentProperty).append(".id = :parentId");
		}
		
		hql.append(" like :search order by ").append(property);
		Query query = getSession().createQuery(hql.toString())
				.setParameter("search", "%" + search  + "%");
		
		if (parentProperty != null) {
			query.setParameter("parentId", ScreenContext.get(request).getParentId());	
		}
		return query.list();
	}

}
