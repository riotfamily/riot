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
package org.riotfamily.statistics.commands;

import org.hibernate.SessionFactory;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public abstract class AbstractHibernateCacheCommand extends AbstractCommand {

	private SessionFactory sessionFactory;
	
	public AbstractHibernateCacheCommand(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		doExecute(context, selection);
		
		return new RefreshListResult();
	}
	
	protected abstract void doExecute(CommandContext context, Selection selection);

	protected void clearCache(String entityPrefix, boolean inverse) {
		String[] entityNames = sessionFactory.getStatistics().getEntityNames();
		for (String name : entityNames) {
			boolean match = name.startsWith(entityPrefix);
			if (match ^ inverse) {
				evictCacheEntry(name, false);
			}
		}
	}
	
	protected void evictCacheEntry(String entity, boolean collection) {
		if (collection) {
			sessionFactory.evictCollection(entity);
		} 
		else {
			Class<?> clazz = SpringUtils.classForName(entity);
			sessionFactory.evict(clazz);
		}
	}
}
