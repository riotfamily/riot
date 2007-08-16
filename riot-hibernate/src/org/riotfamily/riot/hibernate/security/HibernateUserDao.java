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
package org.riotfamily.riot.hibernate.security;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.util.HashUtils;
import org.riotfamily.riot.hibernate.dao.HqlDao;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.riotfamily.riot.security.auth.RiotUser;
import org.riotfamily.riot.security.dao.RiotUserDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * RiotUserDao that performs look-ups vie Hibernate.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HibernateUserDao extends HqlDao implements RiotUserDao, 
		InitializingBean {

	public static final String DEFAULT_USERNAME = "admin";
	
	public static final String DEFAULT_PASSWORD = "admin";
	
	private String usernameProperty = "id";
	
	private String passwordProperty = "password";
	
	private String newPasswordProperty = "newPassword";
	
	private boolean hashPasswords = true;
	
	private RiotUser initialUser;
	
	/**
	 * Sets the user class.
	 * @throws IllegalArgumentException if the given class does not implement 
	 *         the {@link RiotUser} interface.
	 */
	public void setEntityClass(Class entityClass) {
		Assert.isAssignable(RiotUser.class, entityClass);
		super.setEntityClass(entityClass);
	}
	
	/**
	 * Sets the name of the property that holds the username. This property is
	 * used by {@link #findUserByCredentials(String, String)} to look up
	 * a user upon login.
	 */
	public void setUsernameProperty(String usernameProperty) {
		Assert.notNull(usernameProperty);
		this.usernameProperty = usernameProperty;
	}

	/**
	 * Sets the name of the property that holds the (possibly hashed) password.
	 * This property is used by {@link #findUserByCredentials(String, String)} 
	 * to look up a user upon login.
	 */
	public void setPasswordProperty(String passwordProperty) {
		Assert.notNull(passwordProperty);
		this.passwordProperty = passwordProperty;
	}
	
	/**
	 * Sets whether MD5 hashes should be used instead of plain text passwords.
	 */
	public void setHashPasswords(boolean hashPasswords) {
		this.hashPasswords = hashPasswords;
	}

	/**
	 * Sets the name of the (transient) property that holds the new plain text
	 * password. When {@link #setHashPasswords(boolean) hashed passwords} are
	 * used, this property is checked upon updates. If the property contains a
	 * non null value, this value is used to create a new password hash.
	 */
	public void setNewPasswordProperty(String newPasswordProperty) {
		this.newPasswordProperty = newPasswordProperty;
	}

	/**
	 * Sets the initial user object that is persisted when no other user exists.
	 * If set to <code>null</code> (default), a new instance of the 
	 * {@link #setEntityClass(Class) entity class} is created via reflection.
	 */
	public void setInitialUser(RiotUser initialUser) {
		this.initialUser = initialUser;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (initialUser != null) {
			Assert.isInstanceOf(getEntityClass(), initialUser);
		}
		else {
			initialUser = (RiotUser) getEntityClass().newInstance();
			PropertyUtils.setProperty(initialUser, usernameProperty, 
					DEFAULT_USERNAME);
			
			String password = hashPasswords 
					? HashUtils.md5(DEFAULT_PASSWORD) 
					: DEFAULT_PASSWORD;
					
			PropertyUtils.setProperty(initialUser, passwordProperty, password);
		}
	}
	
	public RiotUser findUserByCredentials(String username, String password) {
		if (hashPasswords) {
			password = HashUtils.md5(password);
		}
		Criteria c = createCriteria(getEntityClass())
			.add(Restrictions.eq(usernameProperty, username))
			.add(Restrictions.eq(passwordProperty, password));
			
		RiotUser user = (RiotUser) c.uniqueResult();
		if (user == null && !anyUserExists()) {
			save(initialUser, null);
			String initialUsername = PropertyUtils.getPropertyAsString(initialUser, usernameProperty);
			String initialPassword = PropertyUtils.getPropertyAsString(initialUser, passwordProperty);
			if (initialUsername.equals(username) 
					&& initialPassword.equals(password)) {
				
				return initialUser;
			}
		}
		return user;
	}
	
	protected boolean anyUserExists() {
		return getListSize(null, new ListParamsImpl()) > 0;
	}
	
	protected void hashNewPassword(Object user) {
		if (hashPasswords) {
			String newPassword = PropertyUtils.getPropertyAsString(user, newPasswordProperty);
			if (newPassword != null) {
				String hash = HashUtils.md5(newPassword);
				PropertyUtils.setProperty(user, passwordProperty, hash);
				PropertyUtils.setProperty(user, newPasswordProperty, null);
			}
		}
	}
	
	public void save(Object entity, Object parent) {
		hashNewPassword(entity);
		super.save(entity, parent);
	}
	
	public void update(Object entity) {
		hashNewPassword(entity);
		super.update(entity);
	}

}
