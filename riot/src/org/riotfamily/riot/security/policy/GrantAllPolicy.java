package org.riotfamily.riot.security.policy;

import org.riotfamily.riot.editor.EditorDefinition;

/**
 * Default RiotPolicy that always returns <code>true</code>.
 */
public class GrantAllPolicy implements AuthorizationPolicy {
    
	private int order = Integer.MAX_VALUE;
	
    public int getOrder() {
		return this.order;
	}
	
    public void setOrder(int order) {
		this.order = order;
	}

	public int checkPermission(String subject, String action, Object object, 
    		EditorDefinition editor) {
    	
        return ACCESS_GRANTED;
    }

}
