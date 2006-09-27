package org.riotfamily.riot.hibernate.security;

import org.riotfamily.common.util.HashUtils;

public class User {

	private String id;
	
	private String role;
	
	private String password;
	
	private String name;
	
	private String email;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
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
	
	public void setNewPassword(String password) {
		if (password != null) {
			this.password = HashUtils.md5(password);
		}
		else if (this.password == null) {
			throw new IllegalStateException("A password must be set!");
		}
	}
	
	public String getNewPassword() {
		return null;
	}
	
	public boolean isvalidPassword(String pw) {
		return pw != null && HashUtils.md5(pw).equals(this.password);
	}

}
