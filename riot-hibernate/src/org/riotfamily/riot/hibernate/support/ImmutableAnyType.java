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
package org.riotfamily.riot.hibernate.support;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.springframework.util.ObjectUtils;

/**
 * Hibernate UserType that can store any Java type which:
 * <ul>
 *   <li>is immutable</li>
 *   <li>implements serializable</li>
 *   <li>implements a toString() method</li>
 *   <li>either has a <code>public static valueOf(String)</code> method or 
 *   a single <code>String</code>-argument constructor that creates a new 
 *   instance from the <code>toString()</code> representation</li>
 * </ul>
 * <p>
 * The type is stored in two String columns, the first one stores the 
 * class name, the second one stores the <code>toString()</code> representation.
 * </p>
 * <p>
 * This UserType can be used to store any Java primitive type 
 * or enum. When values of this type are put into the 2nd-level cache, 
 * the materialized object is stored, eliminating the need for String 
 * conversion.
 * </p>
 */
public class ImmutableAnyType implements UserType {

	private static final int[] SQL_TYPES = new int[] {
		Hibernate.STRING.sqlType(),
		Hibernate.STRING.sqlType()
	};

	/**
	 * {@inheritDoc}
	 */
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?> returnedClass() {
		return Object.class;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object x, Object y) throws HibernateException {
		return ObjectUtils.nullSafeEquals(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
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

	/**
	 * {@inheritDoc}
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		
		if (value == null) {
			st.setNull(index, Hibernate.STRING.sqlType());
			st.setNull(index + 1, Hibernate.STRING.sqlType());
		}
		else {
			st.setString(index, value.getClass().getName());
			st.setString(index + 1, value.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object deepCopy(Object value) throws HibernateException {
		// No need to copy as the value is immutable
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		
		return cached;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		
		return original;
	}
}
