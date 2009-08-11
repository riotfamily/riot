package org.riotfamily.core.security.session;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.core.security.auth.RiotUser;

/**
 * Class that associates a RiotUser with the current thread.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SecurityContext {

	private static ThreadLocal<RiotUser> threadLocal = new ThreadLocal<RiotUser>();
	
	public static void bindUserToCurrentThread(RiotUser user) {
		threadLocal.set(user);
		RiotLog.put("RiotUser", (user != null) ? user.getUserId() : null);
	}
	
	public static RiotUser getCurrentUser() {
		return threadLocal.get();
	}
	
	public static void resetUser() {
		threadLocal.set(null);
		RiotLog.remove("RiotUser");
	}
	
}
