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
package org.riotfamily.common.web.support;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Utility class that removes a value from a collection when the HTTP session 
 * is invalidated.
 * <p>
 * Use this class if you need to keep a global reference to an object that is
 * bound to a user's session. 
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class SessionReferenceRemover implements HttpSessionBindingListener {

	private Logger log = LoggerFactory.getLogger(SessionReferenceRemover.class);
	
	private static final String SESSION_KEY = 
			SessionReferenceRemover.class.getName();
	
	private ArrayList<SessionReference> references = new ArrayList<SessionReference>();
	
	
	private SessionReferenceRemover() {
	}
	
	private void addReference(Collection<?> collection, Object value) {
		references.add(new SessionReference(collection, value));
	}

	public void valueBound(HttpSessionBindingEvent event) {
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		log.info("Session invalidated - removing " + references.size() 
				+ " references.");
		
		for (SessionReference ref : references) {
			ref.remove();
		}
		references.clear();
	}
	
	/**
	 * Removes the given value from the collection when the session is
	 * invalidated.
	 */
	public static void removeFromCollectionOnInvalidation(
			HttpSession session, Collection<?> collection, Object value) {
		
		SessionReferenceRemover remover = (SessionReferenceRemover) 
				session.getAttribute(SESSION_KEY);
		
		if (remover == null) {
			remover = new SessionReferenceRemover();
			session.setAttribute(SESSION_KEY, remover);
		}
		remover.addReference(collection, value);
	}
	
	private static class SessionReference {

		private Collection<?> collection;
		
		private Object value;

		public SessionReference(Collection<?> collection, Object value) {
			this.collection = collection;
			this.value = value;
		}
		
		public void remove() {
			collection.remove(value);
		}
		
	}
	
}
