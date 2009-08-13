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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.dom4j.Node;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.AbstractType;
import org.hibernate.type.Type;
import org.hibernate.util.EqualsHelper;

public class ImmutableAnyType extends AbstractType {

	public static final Type INSTANCE = new ImmutableAnyType();
	
	private static final int[] SQL_TYPES = new int[] {
		Hibernate.STRING.sqlType(),
		Hibernate.STRING.sqlType()
	};
	
	private static final boolean[] BOTH = new boolean[] {true, true};
	
	private static final boolean[] NONE = new boolean[] {false, false};


	public String getName() {
		return "immutable_any";
	}

	public Class<?> getReturnedClass() {
		return Object.class;
	}

	public int[] sqlTypes(Mapping mapping) throws MappingException {
		return SQL_TYPES;
	}
	
	public int getColumnSpan(Mapping mapping) throws MappingException {
		return 2;
	}
	
	public boolean[] toColumnNullness(Object value, Mapping mapping) {
		return value == null ? BOTH : NONE;
	}
	
	public boolean isMutable() {
		return false;
	}
	
	public boolean isDirty(Object old, Object current, boolean[] checkable,
			SessionImplementor session) throws HibernateException {

		return !EqualsHelper.equals(old, current);
	}

	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		String type = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
		if (type == null) {
			return null;
		}

		String value = (String) Hibernate.STRING.nullSafeGet(rs, names[1]);
		if (value == null) {
			return null;
		}

		try {
			Class<?> clazz = Class.forName(type);
			if (clazz.equals(String.class)) {
				return value;
			}
			else if (clazz.equals(Character.class)) {
				return value.charAt(0);
			}
			else {
				try {
					Method m = clazz.getMethod("valueOf", String.class);
					return m.invoke(null, value);
				}
				catch (NoSuchMethodException ex) {
					Constructor<?> c = clazz.getConstructor(String.class);
					return c.newInstance(value);
				}
			}
		}
		catch (ClassNotFoundException ex) {
			throw new HibernateException(ex);
		}
		catch (NoSuchMethodException ex) {
			throw new HibernateException(
					"Class " + type + " does not have a valueOf(String) method"
					+ " or a constructor with a single String argument");
		}
		catch (InvocationTargetException ex) {
			throw new HibernateException(type + ".valueOf(\"" + value
					+ "\") threw an exception: " + ex.getCause(), ex);
		}
		catch (InstantiationException ex) {
			throw new HibernateException(type + "(\"" + value
					+ "\") threw an exception:  " + ex.getCause(), ex);
		}
		catch (IllegalAccessException ex) {
			throw new HibernateException("Unable to invoke " + type
					+ ".valueOf(String)", ex);
		}
	}

	public Object nullSafeGet(ResultSet rs, String name,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		
		throw new HibernateException("Type requires two columns");
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		
		if (value == null) {
			st.setNull(index, Hibernate.STRING.sqlType());
			st.setNull(index + 1, Hibernate.STRING.sqlType());
		}
		else {
			st.setString(index, value.getClass().getName());
			st.setString(index + 1, value.toString());
		}
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index,
			boolean[] settable, SessionImplementor session)
			throws HibernateException, SQLException {
		
		if (settable[0]) {
			nullSafeSet(st, value, index, session);
		}
	}

	public Object deepCopy(Object value, EntityMode entityMode,
			SessionFactoryImplementor factory) throws HibernateException {
		
		// No need to copy as the value is immutable
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner, Map copyCache)
			throws HibernateException {

		return original;
	}

	public String toLoggableString(Object value,
			SessionFactoryImplementor factory)
			throws HibernateException {
		
		return value == null ? "null" : value.toString();
	}
	
	public Object fromXMLNode(Node xml, Mapping factory)
			throws HibernateException {
	
		throw new HibernateException("XML is not supported by this type");
	}
	
	public void setToXMLNode(Node node, Object value,
			SessionFactoryImplementor factory) throws HibernateException {
	
		throw new HibernateException("XML is not supported by this type");
	}

}
