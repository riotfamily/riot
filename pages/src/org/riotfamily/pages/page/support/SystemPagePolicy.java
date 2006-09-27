package org.riotfamily.pages.page.support;

import org.riotfamily.pages.page.Page;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;

/**
 * AuthorizationPolicy that denies the use of the cut and delete commands
 * on system pages.
 */
public class SystemPagePolicy implements AuthorizationPolicy {

	private int order = Integer.MAX_VALUE - 1;

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public int checkPermission(String subject, String action, Object object, 
			EditorDefinition editor) {
		
		if (object instanceof Page && ("cut".equals(action) 
				|| "delete".equals(action))) {
			
			Page page = (Page) object;
			if (page.isSystemPage()) {
				return ACCESS_DENIED;
			}
		}
		return ACCESS_ABSTAIN;
	}
}
