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
package org.riotfamily.riot.hibernate.security;

import java.util.List;

import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.session.SessionMetaDataStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PersistentSessionMetaDataStore 
		implements SessionMetaDataStore<PersistentSessionMetaData> {

	public List<PersistentSessionMetaData> listAll() {
		return PersistentSessionMetaData.findAll();
	}

	public void sessionEnded(PersistentSessionMetaData data) {
		data = data.merge();
		data.sessionEnded();
	}

	public PersistentSessionMetaData sessionStarted(String userName, RiotUser user, 
			String loginIP) {
		
		PersistentSessionMetaData meta = PersistentSessionMetaData.loadByUser(user); 
		if (meta == null) {
			meta = new PersistentSessionMetaData(user);
			meta.save();
		}
		meta.sessionStarted(userName, loginIP);
		return meta;
	}

}
