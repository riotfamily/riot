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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.hibernate;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * Convenient base class for callbacks that don't return a result. It also
 * allows the callback code to throw checked exceptions which are automatically
 * wrapped into a {@link RuntimeException}.
 * 
 * @since 9.0
 */
public abstract class HibernateCallbackWithoutResult implements HibernateCallback {

	public final Object doInHibernate(Session session) 
			throws HibernateException, SQLException {
		
		try {
			doWithoutResult(session);
		}
		catch (HibernateException e) {
			throw e;
		}
		catch (SQLException e) {
			throw e;
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	protected abstract void doWithoutResult(Session session) throws Exception;

}
