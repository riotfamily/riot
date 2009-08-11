package org.riotfamily.riot.hibernate.security;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.core.security.auth.RiotUser;

@Entity
@Table(name="riot_users")
public class User extends ActiveRecord implements RiotUser {

	private String id;
	
	private String password;
	
	private transient String newPassword;
	
	private String name;
	
	private String email;

	@Id
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Transient
	public String getUserId() {
		return getId();
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Transient
	public String getNewPassword() {
		return this.newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (id != null && getClass().isInstance(obj)) {
			User other = (User) obj;
			return id.equals(other.getId());
		}
		return false;
	}
	
}
