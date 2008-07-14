/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.ScrollableResults;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.EventSource;
import org.hibernate.hql.ParameterTranslations;
import org.hibernate.hql.QueryExecutionRequestException;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
import org.hibernate.type.Type;

public class CacheQueryTranslatorFactory extends ASTQueryTranslatorFactory {

	@Override
	@SuppressWarnings("unchecked")
	public QueryTranslator createQueryTranslator(String queryIdentifier,
			String queryString, Map filters, SessionFactoryImplementor factory) {
	
		QueryTranslator qt = super.createQueryTranslator(queryIdentifier, queryString, filters, factory);
		return new CacheQueryTranslator(qt);
	}
	
	private static class CacheQueryTranslator implements QueryTranslator {
		
		private QueryTranslator delegate;

		public CacheQueryTranslator(QueryTranslator delegate) {
			this.delegate = delegate;
		}

		public List collectSqlStrings() {
			return delegate.collectSqlStrings();
		}

		public void compile(Map replacements, boolean shallow) throws QueryException,
				MappingException {
			delegate.compile(replacements, shallow);
		}

		public boolean containsCollectionFetches() {
			return delegate.containsCollectionFetches();
		}

		public int executeUpdate(QueryParameters params, SessionImplementor impl)
				throws HibernateException {
			return delegate.executeUpdate(params, impl);
		}

		public String[][] getColumnNames() {
			return delegate.getColumnNames();
		}

		public Map getEnabledFilters() {
			return delegate.getEnabledFilters();
		}

		public ParameterTranslations getParameterTranslations() {
			return delegate.getParameterTranslations();
		}

		public String getQueryIdentifier() {
			return delegate.getQueryIdentifier();
		}

		public Set getQuerySpaces() {
			return delegate.getQuerySpaces();
		}

		public String getQueryString() {
			return delegate.getQueryString();
		}

		public String[] getReturnAliases() {
			return delegate.getReturnAliases();
		}

		public Type[] getReturnTypes() {
			return delegate.getReturnTypes();
		}

		public String getSQLString() {
			return delegate.getSQLString();
		}

		public boolean isManipulationStatement() {
			return delegate.isManipulationStatement();
		}

		public Iterator iterate(QueryParameters params, EventSource eventSource)
				throws HibernateException {
			return delegate.iterate(params, eventSource);
		}

		public List list(SessionImplementor impl, QueryParameters params)
				throws HibernateException {
			
			try {
				Type[] types = getReturnTypes();
				if (types != null && types.length == 1) {
					Class<?> clazz = types[0].getReturnedClass();
					if (clazz.isAnnotationPresent(TagCacheItems.class)) {
						CacheTagUtils.tag(clazz);
					}
				}
			}
			catch (QueryExecutionRequestException e) {
			}
			return delegate.list(impl, params);
		}

		public ScrollableResults scroll(QueryParameters params,
				SessionImplementor impl) throws HibernateException {
			return delegate.scroll(params, impl);
		}

		public void validateScrollability() throws HibernateException {
			delegate.validateScrollability();
		}
		
	}
}
