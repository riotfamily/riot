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
package org.riotfamily.dbmsgsrc.dao;


public class HibernateMessageSourceDao {

	/*
	public MessageBundleEntry findEntry(String bundle, String code) {
		return (MessageBundleEntry) hibernate.createCacheableCriteria(
				MessageBundleEntry.class)
				.add(Restrictions.naturalId()
					.set("bundle", bundle)
					.set("code", code))
				.uniqueResult();
	}

	@Transactional
	public void saveEntry(MessageBundleEntry entry) {
		hibernate.save(entry);
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public void removeEmptyEntries(String bundle) {
		List<MessageBundleEntry> entries = hibernate.createCacheableCriteria(
				MessageBundleEntry.class)
				.add(Restrictions.sizeLe("messages", 1))
				.add(Restrictions.naturalId().set("bundle", bundle))
				.list();
		
		for (MessageBundleEntry entry : entries) {
			hibernate.delete(entry);
		}
	}
	 */
}
