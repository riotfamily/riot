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
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;

/**
 * Implementation of the {@link Query} interface that provides methods with 
 * generic return types, i.e. {@link #load()} and {@link #find()}.
 * <p>
 * It also adds the {@link #cache()} method to enable query caching and set
 * the region to a synthetic name.
 * <p>
 * The {@link #intResult()} method can be used to obtain the result of 
 * <code>select count(*) ...</code> queries. 
 */
public class TypedQuery<T> implements Query {

	private Query query;
	
	private Class<T> type;
	
	public TypedQuery(Query query, Class<T> type) {
		this.query = query;
		this.type = type;
	}

	/**
	 * Calls <code>{@link #setCacheable(boolean) setCacheable}(true)</code> and 
	 * <code>{@link #setCacheRegion(String) setCacheRegion}(type.getName() + "QueryCache")</code>.
	 */
	public TypedQuery<T> cache() {
		setCacheable(true);
		setCacheRegion(type.getName() + "QueryCache");
		return this;
	}
	
	/**
	 * Generic version of {@link #uniqueResult()}
	 */
	@SuppressWarnings("unchecked")
	public T load() {
		return (T) uniqueResult();
	}
	
	/**
	 * Generic version of {@link #list()}. Returns a new {@link QueryResult} 
	 * for underlying query and the generic type.
	 */
	public List<T> find() {
		return new QueryResult<T>(query, type);
	}

	/**
	 * Sets firstResult and maxResults in a single call.
	 */
	public TypedQuery<T> limit(int first, int max) {
		query.setFirstResult(first).setMaxResults(max);
		return this;
	}

	/**
	 * Casts the {@link #uniqueResult()} to <code>java.lang.Number</code> and
	 * returns the <code>intValue()</code>, or <code>0</code> if the result is null.
	 */
	public int intResult() {
		Number result = (Number) query.uniqueResult();
		return result != null ? result.intValue() : 0;
	}
	
	/**
	 * Returns a new {@link QueryResult} for underlying query. 
	 */
	public List<?> list() throws HibernateException {
		return new QueryResult<T>(query);
	}
	
	// -----------------------------------------------------------------------
	// Plain delegate methods
	// -----------------------------------------------------------------------
	
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

	public TypedQuery<T> setBigDecimal(int position, BigDecimal number) {
		query.setBigDecimal(position, number);
		return this;
	}

	public TypedQuery<T> setBigDecimal(String name, BigDecimal number) {
		query.setBigDecimal(name, number);
		return this;
	}

	public TypedQuery<T> setBigInteger(int position, BigInteger number) {
		query.setBigInteger(position, number);
		return this;
	}

	public TypedQuery<T> setBigInteger(String name, BigInteger number) {
		query.setBigInteger(name, number);
		return this;
	}

	public TypedQuery<T> setBinary(int position, byte[] val) {
		query.setBinary(position, val);
		return this;
	}

	public TypedQuery<T> setBinary(String name, byte[] val) {
		query.setBinary(name, val);
		return this;
	}

	public TypedQuery<T> setBoolean(int position, boolean val) {
		query.setBoolean(position, val);
		return this;
	}

	public TypedQuery<T> setBoolean(String name, boolean val) {
		query.setBoolean(name, val);
		return this;
	}

	public TypedQuery<T> setByte(int position, byte val) {
		query.setByte(position, val);
		return this;
	}

	public TypedQuery<T> setByte(String name, byte val) {
		query.setByte(name, val);
		return this;
	}

	public TypedQuery<T> setCacheable(boolean cacheable) {
		query.setCacheable(cacheable);
		return this;
	}

	public TypedQuery<T> setCacheMode(CacheMode cacheMode) {
		query.setCacheMode(cacheMode);
		return this;
	}

	public TypedQuery<T> setCacheRegion(String cacheRegion) {
		query.setCacheRegion(cacheRegion);
		return this;
	}

	public TypedQuery<T> setCalendar(int position, Calendar calendar) {
		query.setCalendar(position, calendar);
		return this;
	}

	public TypedQuery<T> setCalendar(String name, Calendar calendar) {
		query.setCalendar(name, calendar);
		return this;
	}

	public TypedQuery<T> setCalendarDate(int position, Calendar calendar) {
		query.setCalendarDate(position, calendar);
		return this;
	}

	public TypedQuery<T> setCalendarDate(String name, Calendar calendar) {
		query.setCalendarDate(name, calendar);
		return this;
	}

	public TypedQuery<T> setCharacter(int position, char val) {
		query.setCharacter(position, val);
		return this;
	}

	public TypedQuery<T> setCharacter(String name, char val) {
		query.setCharacter(name, val);
		return this;
	}

	public TypedQuery<T> setComment(String comment) {
		query.setComment(comment);
		return this;
	}

	public TypedQuery<T> setDate(int position, Date date) {
		query.setDate(position, date);
		return this;
	}

	public TypedQuery<T> setDate(String name, Date date) {
		query.setDate(name, date);
		return this;
	}

	public TypedQuery<T> setDouble(int position, double val) {
		query.setDouble(position, val);
		return this;
	}

	public TypedQuery<T> setDouble(String name, double val) {
		query.setDouble(name, val);
		return this;
	}

	public TypedQuery<T> setEntity(int position, Object val) {
		query.setEntity(position, val);
		return this;
	}

	public TypedQuery<T> setEntity(String name, Object val) {
		query.setEntity(name, val);
		return this;
	}

	public TypedQuery<T> setFetchSize(int fetchSize) {
		query.setFetchSize(fetchSize);
		return this;
	}

	public TypedQuery<T> setFirstResult(int firstResult) {
		query.setFirstResult(firstResult);
		return this;
	}

	public TypedQuery<T> setFloat(int position, float val) {
		query.setFloat(position, val);
		return this;
	}

	public TypedQuery<T> setFloat(String name, float val) {
		query.setFloat(name, val);
		return this;
	}

	public TypedQuery<T> setFlushMode(FlushMode flushMode) {
		query.setFlushMode(flushMode);
		return this;
	}

	public TypedQuery<T> setInteger(int position, int val) {
		query.setInteger(position, val);
		return this;
	}

	public TypedQuery<T> setInteger(String name, int val) {
		query.setInteger(name, val);
		return this;
	}

	public TypedQuery<T> setLocale(int position, Locale locale) {
		query.setLocale(position, locale);
		return this;
	}

	public TypedQuery<T> setLocale(String name, Locale locale) {
		query.setLocale(name, locale);
		return this;
	}

	public TypedQuery<T> setLockMode(String alias, LockMode lockMode) {
		query.setLockMode(alias, lockMode);
		return this;
	}

	public TypedQuery<T> setLong(int position, long val) {
		query.setLong(position, val);
		return this;
	}

	public TypedQuery<T> setLong(String name, long val) {
		query.setLong(name, val);
		return this;
	}

	public TypedQuery<T> setMaxResults(int maxResults) {
		query.setMaxResults(maxResults);
		return this;
	}

	public TypedQuery<T> setParameter(int position, Object val, Type type) {
		query.setParameter(position, val, type);
		return this;
	}

	public TypedQuery<T> setParameter(int position, Object val)
			throws HibernateException {
		
		query.setParameter(position, val);
		return this;
	}

	public TypedQuery<T> setParameter(String name, Object val, Type type) {
		query.setParameter(name, val, type);
		return this;
	}

	public TypedQuery<T> setParameter(String name, Object val) throws HibernateException {
		query.setParameter(name, val);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TypedQuery<T> setParameterList(String name, Collection vals, Type type) throws HibernateException {
		query.setParameterList(name, vals, type);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TypedQuery<T> setParameterList(String name, Collection vals) throws HibernateException {
		query.setParameterList(name, vals);
		return this;
	}

	public TypedQuery<T> setParameterList(String name, Object[] vals, Type type) throws HibernateException {
		query.setParameterList(name, vals, type);
		return this;
	}

	public TypedQuery<T> setParameterList(String name, Object[] vals) throws HibernateException {
		query.setParameterList(name, vals);
		return this;
	}

	public TypedQuery<T> setParameters(Object[] values, Type[] types) throws HibernateException {
		query.setParameters(values, types);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TypedQuery<T> setProperties(Map bean) throws HibernateException {
		query.setProperties(bean);
		return this;
	}

	public TypedQuery<T> setProperties(Object bean) throws HibernateException {
		query.setProperties(bean);
		return this;
	}

	public TypedQuery<T> setReadOnly(boolean readOnly) {
		query.setReadOnly(readOnly);
		return this;
	}

	public TypedQuery<T> setResultTransformer(ResultTransformer transformer) {
		query.setResultTransformer(transformer);
		return this;
	}

	public TypedQuery<T> setSerializable(int position, Serializable val) {
		query.setSerializable(position, val);
		return this;
	}

	public TypedQuery<T> setSerializable(String name, Serializable val) {
		query.setSerializable(name, val);
		return this;
	}

	public TypedQuery<T> setShort(int position, short val) {
		query.setShort(position, val);
		return this;
	}

	public TypedQuery<T> setShort(String name, short val) {
		query.setShort(name, val);
		return this;
	}

	public TypedQuery<T> setString(int position, String val) {
		query.setString(position, val);
		return this;
	}

	public TypedQuery<T> setString(String name, String val) {
		query.setString(name, val);
		return this;
	}

	public TypedQuery<T> setText(int position, String val) {
		query.setText(position, val);
		return this;
	}

	public TypedQuery<T> setText(String name, String val) {
		query.setText(name, val);
		return this;
	}

	public TypedQuery<T> setTime(int position, Date date) {
		query.setTime(position, date);
		return this;
	}

	public TypedQuery<T> setTime(String name, Date date) {
		query.setTime(name, date);
		return this;
	}

	public TypedQuery<T> setTimeout(int timeout) {
		query.setTimeout(timeout);
		return this;
	}

	public TypedQuery<T> setTimestamp(int position, Date date) {
		query.setTimestamp(position, date);
		return this;
	}

	public TypedQuery<T> setTimestamp(String name, Date date) {
		query.setTimestamp(name, date);
		return this;
	}

	public Object uniqueResult() throws HibernateException {
		return query.uniqueResult();
	}

	public boolean isReadOnly() {
		return query.isReadOnly();
	}

	public TypedQuery<T> setLockOptions(LockOptions lockOptions) {
		query.setLockOptions(lockOptions);
		return this;
	}
	
}
