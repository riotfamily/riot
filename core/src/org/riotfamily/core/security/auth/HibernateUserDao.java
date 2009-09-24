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
package org.riotfamily.core.security.auth;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.util.HashUtils;
import org.riotfamily.core.dao.hibernate.HqlDao;
import org.riotfamily.core.screen.list.ListParamsImpl;
import org.springframework.util.Assert;

/**
 * RiotUserDao that performs look-ups via Hibernate.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class HibernateUserDao extends HqlDao 
		implements RiotUserDao {

	public static final String DEFAULT_USERNAME = "admin";
	
	public static final String DEFAULT_PASSWORD = "admin";
	
	private String usernameProperty = "id";
	
	private String passwordProperty = "password";
	
	private String newPasswordProperty = "newPassword";
	
	private boolean hashPasswords = true;
	
	private RiotUser initialUser;
	
	public HibernateUserDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	/**
	 * Sets the user class.
	 * @throws IllegalArgumentException if the given class does not implement 
	 *         the {@link RiotUser} interface.
	 */
	@Override
	public void setEntityClass(Class<?> entityClass) {
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
	 * Sets whether hashed passwords should be used instead of plain text.
	 * Default is <code>true</code>.
	 * @see #hashPassword(String)
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
	
	/**
	 * Creates (or validates) the initial user.
	 * <p>
	 * Note: The user is not saved to the database at this point, as this 
	 * method is not invoked within a transaction. The user will be persisted
	 * when {@link #findUserByCredentials(String, String)} or 
	 * {@link #findUserById(String)} is called and the database does not 
	 * contain any user objects.
	 * </p> 
	 */
	@Override
	protected void initDao() throws Exception {
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
		super.initDao();
	}
	
	/**
	 * Hashes the given password. The default implementation creates a MD5
	 * sum. Subclasses may overwrite this method to use a different algorithm
	 * or add a salt.
	 */
	protected String hashPassword(String plainText) {
		return HashUtils.md5(plainText);
	}
	
	public void updatePassword(RiotUser user, String newPassword) {
		PropertyUtils.setProperty(user, newPasswordProperty, newPassword);
		hashNewPassword(user);
		getSession().update(user);
	}
	
	/**
	 * Performs a database lookup with the given credentials. If no matching 
	 * user is found, {@link #findInitialUser(String, String)} is called.
	 */
	public RiotUser findUserByCredentials(String username, String password) {
		if (hashPasswords) {
			password = hashPassword(password);
		}
		Criteria c = getSession().createCriteria(getEntityClass())
			.add(Restrictions.eq(usernameProperty, username))
			.add(Restrictions.eq(passwordProperty, password));
			
		RiotUser user = (RiotUser) c.uniqueResult();
		if (user == null) {
			user = findInitialUser(username, password);
		}
		return user;
	}
	
	/**
	 * If no user exists, the given credentials are compared with the ones of 
	 * the initial user. If username and password match, the initial user is 
	 * persisted and returned. 
	 */
	protected RiotUser findInitialUser(String username, String password) {
		if (!anyUserExists()) {
			save(initialUser, null);
			
			String initialUsername = PropertyUtils.getPropertyAsString(initialUser, usernameProperty);
			Assert.notNull(initialUsername, "The initial user's '" + usernameProperty + "' property must not be null.");
			
			String initialPassword = PropertyUtils.getPropertyAsString(initialUser, passwordProperty);
			Assert.notNull(initialPassword, "The initial user's '" + passwordProperty + "' property  must not be null.");
			
			if (initialUsername.equals(username) && initialPassword.equals(password)) {
				return initialUser;
			}
		}
		return null;
	}
	
	/**
	 * Performs a database lookup with the given userId. If no matching 
	 * user is found, {@link #findInitialUser(String)} is called.
	 */
	public RiotUser findUserById(String userId) {
		RiotUser user = (RiotUser) load(userId);
		if (user == null) {
			user = findInitialUser(userId);
		}
		return user;
	}
	
	/**
	 * If no user exists, the given userId is compared with the one of the 
	 * initial user. If the id matches, the initial user is persisted and 
	 * returned. 
	 */
	protected RiotUser findInitialUser(String userId) {
		if (!anyUserExists() && userId.equals(initialUser.getUserId())) {
			save(initialUser, null);
			return initialUser;
		}
		return null;
	}
	
	/**
	 * Returns whether any user exists in the database.
	 */
	protected boolean anyUserExists() {
		return list(null, new ListParamsImpl()).size() > 0;
	}
	
	/**
	 * If {@link #setHashPasswords(boolean) hashed passwords} are enabled,
	 * this method checks whether the {@link #setNewPasswordProperty(String)
	 * newPassword} property contains a non-null value and sets the value of
	 * the {@link #setPasswordProperty(String) password} property to the
	 * {@link #hashPassword(String) hashed value}.
	 */
	private void hashNewPassword(Object user) {
		if (hashPasswords) {
			String newPassword = PropertyUtils.getPropertyAsString(user, newPasswordProperty);
			if (newPassword != null) {
				String hash = hashPassword(newPassword);
				PropertyUtils.setProperty(user, passwordProperty, hash);
				PropertyUtils.setProperty(user, newPasswordProperty, null);
			}
		}
	}

	/**
	 * Invokes {@link #hashNewPassword(Object)} and delegates the call to the
	 * super method.
	 */
	public void save(Object entity, Object parent) {
		hashNewPassword(entity);
		super.save(entity, parent);
	}
	
	/**
	 * Invokes {@link #hashNewPassword(Object)} and delegates the call to the
	 * super method.
	 */
	public Object update(Object entity) {
		hashNewPassword(entity);
		return super.update(entity);
	}

}
