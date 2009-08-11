package org.riotfamily.pages.riot.security;

import static org.riotfamily.core.security.policy.AuthorizationPolicy.Permission.ABSTAIN;
import static org.riotfamily.core.security.policy.AuthorizationPolicy.Permission.DENIED;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.policy.ReflectionPolicy;
import org.riotfamily.pages.model.Page;

public class MasterPagePolicy extends ReflectionPolicy {
	
	public MasterPagePolicy() {
		setOrder(Integer.MAX_VALUE - 2);
	}
	
	public Permission translatePage(RiotUser user, Page page, CommandContext context) {
		if (context.getParent() == null 
				|| page.getSite().equals(context.getParent())) {
			
			return DENIED;
		}
		return ABSTAIN;
	}
	
	public Permission getPermission(RiotUser user, String action, 
			Page page, CommandContext context) {
		
		if (context.getParent() != null 
				&& !page.getSite().equals(context.getParent())) {
			
			return DENIED;
		}
		return ABSTAIN;
	}
	
}
