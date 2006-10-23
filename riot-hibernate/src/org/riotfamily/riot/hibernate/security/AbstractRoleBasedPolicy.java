package org.riotfamily.riot.hibernate.security;

import java.util.HashMap;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.security.LoginManager;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class AbstractRoleBasedPolicy extends HibernateDaoSupport 
		implements AuthorizationPolicy {

	private int order = Integer.MAX_VALUE - 1;
	
	private HashMap roles = new HashMap();
	
    public int getOrder() {
		return this.order;
	}
	
    public void setOrder(int order) {
		this.order = order;
	}
    
	public final int checkPermission(String subject, String action, 
			Object object, EditorDefinition editor) {
		
		if (LoginManager.ACTION_LOGIN.equals(action)) {
			invalidateRole(subject);
		}
		return checkRolePermission(getRole(subject), action, object, editor);
	}
	
	protected void invalidateRole(String userId) {
		roles.remove(userId);
	}
	
	protected String getRole(String userId) {
		String role = (String) roles.get(userId);
		if (role == null) {
			User user = (User) getHibernateTemplate().load(User.class, userId);
			role = user.getRole();
			roles.put(userId, role);
		}
		return role;
	}
	
	protected abstract int checkRolePermission(String role, String action, 
			Object object, EditorDefinition editor);
	
}
