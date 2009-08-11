package org.riotfamily.core.security.session;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface that provides meta data about the last and current session.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface SessionMetaData extends Serializable {

	public String getUserId();

	public String getUserName();

	public Date getLastLoginDate();

	public String getLastLoginIP();
	
}
