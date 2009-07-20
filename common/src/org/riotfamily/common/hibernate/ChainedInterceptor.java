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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.hibernate;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.Type;


/**
 * Hibernate {@link Interceptor} that allows the chaining of multiple 
 * implementations.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class ChainedInterceptor extends EmptyInterceptor 
		implements SessionFactoryAwareInterceptor {

	private Set<Interceptor> interceptors = Collections.emptySet();

	public void setInterceptors(Set<Interceptor> interceptors) {
		if (interceptors == null) {
			interceptors = Collections.emptySet();
		}
		this.interceptors = interceptors;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		for (Interceptor interceptor : interceptors) {
			if (interceptor instanceof SessionFactoryAwareInterceptor) {
				((SessionFactoryAwareInterceptor) interceptor).setSessionFactory(sessionFactory);
			}
		}
	}
	
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		for (Interceptor interceptor : interceptors) {
			interceptor.onDelete(entity, id, state, propertyNames, types);
		}
	}

	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onLoad(entity, id, state, propertyNames, types);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void postFlush(Iterator entities) {
		for (Interceptor interceptor : interceptors) {
			interceptor.postFlush(entities);
		}
	}

	@SuppressWarnings("unchecked")
	public void preFlush(Iterator entities) {
		for (Interceptor interceptor : interceptors) {
			interceptor.preFlush(entities);
		}
	}

	public Boolean isTransient(Object entity) {
		Boolean result = null;
		for (Interceptor interceptor : interceptors) {
			result = interceptor.isTransient(entity);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	public void afterTransactionBegin(Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.afterTransactionBegin(tx);
		}
	}

	public void afterTransactionCompletion(Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.afterTransactionCompletion(tx);
		}
	}

	public void beforeTransactionCompletion(Transaction tx) {
		for (Interceptor interceptor : interceptors) {
			interceptor.beforeTransactionCompletion(tx);
		}
	}

	public String onPrepareStatement(String sql) {
		for (Interceptor interceptor : interceptors) {
			sql = interceptor.onPrepareStatement(sql);
		}
		return sql;
	}

	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) throws CallbackException {
		
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onSave(entity, id, state, propertyNames, types);
		}
		return result;
	}

	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		
		boolean result = false;
		for (Interceptor interceptor : interceptors) {
			result |= interceptor.onFlushDirty(entity, id, currentState,
					previousState, propertyNames, types);
		}
		return result;
	}

}
