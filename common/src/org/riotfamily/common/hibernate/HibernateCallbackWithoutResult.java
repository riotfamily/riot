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
public abstract class HibernateCallbackWithoutResult implements HibernateCallback<Object> {

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
