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
package org.riotfamily.dbmsgsrc.riot;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.dao.hibernate.AbstractHqlDao;
import org.riotfamily.dbmsgsrc.DbMessageSource;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.pages.model.Site;

public class LocalMessageDao extends AbstractHqlDao {

	private String bundle = DbMessageSource.DEFAULT_BUNDLE;
	
	public LocalMessageDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public Class<?> getEntityClass() {
		return Message.class;
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	@Override
	protected String getSelect() {
		return "coalesce(lm, dm) as this";
	}

	@Override
	protected String getFrom(ListParams params) {
		return MessageBundleEntry.class.getName()  
				+ " as e left join e.messages lm with lm.text is not null" 
				+ " and lm.locale = :locale"
				+ " join e.messages dm with dm.locale = :default";
	}

	@Override
	protected String getWhere() {
		return "e.bundle = :bundle";
	}

	
	@Override
	protected String getSearchWhereClause(ListParams params) {
		return "((lower(e.code) like :search) or " +
				"(dm is not null and lower(dm.text) like :search) or " +
				"(lower(lm.text) like :search))";
	}
	
	@Override
	protected String getFilterWhereClause(ListParams params) {
		StringBuffer filter = new StringBuffer();
		Map<?, ?> filterMap = (Map<?, ?>) params.getFilter();
		
		if ((Boolean) filterMap.get("notTranslatedOnly")) {
			filter.append("lm is null");
		}
		if ((Boolean) filterMap.get("equalToDefaultText")) {
			if (filter.length() > 0) {
				filter.append(" and ");
			}
			filter.append("lm.text = dm.text");
		}
		return filter.toString();
	}
	
	@Override
	protected String getOrderBy(ListParams params) {
		return "e.code";
	}
	
	@Override
	protected void setFilterParameters(Query query, ListParams params) {
		
	}
	
	@Override
	protected void setQueryParameters(Query query, Object parent,
			ListParams params) {
		
		super.setQueryParameters(query, parent, params);
		Site site = (Site) parent;
		query.setParameter("bundle", bundle);
		query.setParameter("locale", site.getLocale());
		query.setParameter("default", MessageBundleEntry.C_LOCALE);
	}

}
