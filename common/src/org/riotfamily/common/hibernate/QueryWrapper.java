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
package org.riotfamily.common.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

@SuppressWarnings("unchecked")
public class QueryWrapper implements Query {

	private Query query;
	
	public QueryWrapper(Query query) {
		this.query = query;
	}

	public List list() throws HibernateException {
		return new QueryResult(query);
	}
	
	// ------------------------------------------------------------------------
	// Plain delegate methods
	// ------------------------------------------------------------------------
	
	public int executeUpdate() throws HibernateException {
		return query.executeUpdate();
	}

	public String[] getNamedParameters() throws HibernateException {
		return query.getNamedParameters();
	}

	public String getQueryString() {
		return query.getQueryString();
	}

	public String[] getReturnAliases() throws HibernateException {
		return query.getReturnAliases();
	}

	public Type[] getReturnTypes() throws HibernateException {
		return query.getReturnTypes();
	}

	public Iterator<?> iterate() throws HibernateException {
		return query.iterate();
	}

	public ScrollableResults scroll() throws HibernateException {
		return query.scroll();
	}

	public ScrollableResults scroll(ScrollMode scrollMode)
			throws HibernateException {
		return query.scroll(scrollMode);
	}

	public Query setBigDecimal(int position, BigDecimal number) {
		return query.setBigDecimal(position, number);
	}

	public Query setBigDecimal(String name, BigDecimal number) {
		return query.setBigDecimal(name, number);
	}

	public Query setBigInteger(int position, BigInteger number) {
		return query.setBigInteger(position, number);
	}

	public Query setBigInteger(String name, BigInteger number) {
		return query.setBigInteger(name, number);
	}

	public Query setBinary(int position, byte[] val) {
		return query.setBinary(position, val);
	}

	public Query setBinary(String name, byte[] val) {
		return query.setBinary(name, val);
	}

	public Query setBoolean(int position, boolean val) {
		return query.setBoolean(position, val);
	}

	public Query setBoolean(String name, boolean val) {
		return query.setBoolean(name, val);
	}

	public Query setByte(int position, byte val) {
		return query.setByte(position, val);
	}

	public Query setByte(String name, byte val) {
		return query.setByte(name, val);
	}

	public Query setCacheable(boolean cacheable) {
		return query.setCacheable(cacheable);
	}

	public Query setCacheMode(CacheMode cacheMode) {
		return query.setCacheMode(cacheMode);
	}

	public Query setCacheRegion(String cacheRegion) {
		return query.setCacheRegion(cacheRegion);
	}

	public Query setCalendar(int position, Calendar calendar) {
		return query.setCalendar(position, calendar);
	}

	public Query setCalendar(String name, Calendar calendar) {
		return query.setCalendar(name, calendar);
	}

	public Query setCalendarDate(int position, Calendar calendar) {
		return query.setCalendarDate(position, calendar);
	}

	public Query setCalendarDate(String name, Calendar calendar) {
		return query.setCalendarDate(name, calendar);
	}

	public Query setCharacter(int position, char val) {
		return query.setCharacter(position, val);
	}

	public Query setCharacter(String name, char val) {
		return query.setCharacter(name, val);
	}

	public Query setComment(String comment) {
		return query.setComment(comment);
	}

	public Query setDate(int position, Date date) {
		return query.setDate(position, date);
	}

	public Query setDate(String name, Date date) {
		return query.setDate(name, date);
	}

	public Query setDouble(int position, double val) {
		return query.setDouble(position, val);
	}

	public Query setDouble(String name, double val) {
		return query.setDouble(name, val);
	}

	public Query setEntity(int position, Object val) {
		return query.setEntity(position, val);
	}

	public Query setEntity(String name, Object val) {
		return query.setEntity(name, val);
	}

	public Query setFetchSize(int fetchSize) {
		return query.setFetchSize(fetchSize);
	}

	public Query setFirstResult(int firstResult) {
		return query.setFirstResult(firstResult);
	}

	public Query setFloat(int position, float val) {
		return query.setFloat(position, val);
	}

	public Query setFloat(String name, float val) {
		return query.setFloat(name, val);
	}

	public Query setFlushMode(FlushMode flushMode) {
		return query.setFlushMode(flushMode);
	}

	public Query setInteger(int position, int val) {
		return query.setInteger(position, val);
	}

	public Query setInteger(String name, int val) {
		return query.setInteger(name, val);
	}

	public Query setLocale(int position, Locale locale) {
		return query.setLocale(position, locale);
	}

	public Query setLocale(String name, Locale locale) {
		return query.setLocale(name, locale);
	}

	public Query setLockMode(String alias, LockMode lockMode) {
		return query.setLockMode(alias, lockMode);
	}

	public Query setLong(int position, long val) {
		return query.setLong(position, val);
	}

	public Query setLong(String name, long val) {
		return query.setLong(name, val);
	}

	public Query setMaxResults(int maxResults) {
		return query.setMaxResults(maxResults);
	}

	public Query setParameter(int position, Object val, Type type) {
		return query.setParameter(position, val, type);
	}

	public Query setParameter(int position, Object val)
			throws HibernateException {
		return query.setParameter(position, val);
	}

	public Query setParameter(String name, Object val, Type type) {
		return query.setParameter(name, val, type);
	}

	public Query setParameter(String name, Object val)
			throws HibernateException {
		return query.setParameter(name, val);
	}

	public Query setParameterList(String name, Collection vals, Type type)
			throws HibernateException {
		return query.setParameterList(name, vals, type);
	}

	public Query setParameterList(String name, Collection vals)
			throws HibernateException {
		return query.setParameterList(name, vals);
	}

	public Query setParameterList(String name, Object[] vals, Type type)
			throws HibernateException {
		return query.setParameterList(name, vals, type);
	}

	public Query setParameterList(String name, Object[] vals)
			throws HibernateException {
		return query.setParameterList(name, vals);
	}

	public Query setParameters(Object[] values, Type[] types)
			throws HibernateException {
		return query.setParameters(values, types);
	}

	public Query setProperties(Map bean) throws HibernateException {
		return query.setProperties(bean);
	}

	public Query setProperties(Object bean) throws HibernateException {
		return query.setProperties(bean);
	}

	public Query setReadOnly(boolean readOnly) {
		return query.setReadOnly(readOnly);
	}

	public Query setResultTransformer(ResultTransformer transformer) {
		return query.setResultTransformer(transformer);
	}

	public Query setSerializable(int position, Serializable val) {
		return query.setSerializable(position, val);
	}

	public Query setSerializable(String name, Serializable val) {
		return query.setSerializable(name, val);
	}

	public Query setShort(int position, short val) {
		return query.setShort(position, val);
	}

	public Query setShort(String name, short val) {
		return query.setShort(name, val);
	}

	public Query setString(int position, String val) {
		return query.setString(position, val);
	}

	public Query setString(String name, String val) {
		return query.setString(name, val);
	}

	public Query setText(int position, String val) {
		return query.setText(position, val);
	}

	public Query setText(String name, String val) {
		return query.setText(name, val);
	}

	public Query setTime(int position, Date date) {
		return query.setTime(position, date);
	}

	public Query setTime(String name, Date date) {
		return query.setTime(name, date);
	}

	public Query setTimeout(int timeout) {
		return query.setTimeout(timeout);
	}

	public Query setTimestamp(int position, Date date) {
		return query.setTimestamp(position, date);
	}

	public Query setTimestamp(String name, Date date) {
		return query.setTimestamp(name, date);
	}

	public Object uniqueResult() throws HibernateException {
		return query.uniqueResult();
	}

	
}
