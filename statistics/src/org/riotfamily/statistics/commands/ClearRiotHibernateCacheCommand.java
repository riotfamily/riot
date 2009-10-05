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
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.Selection;

public class ClearRiotHibernateCacheCommand 
		extends AbstractHibernateCacheCommand {

	public ClearRiotHibernateCacheCommand(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	@Override
	protected String getIcon(String action) {
		return "clear";
	}

	public void doExecute(CommandContext context, Selection selection) {
		clearCache("org.riotfamily", false); 
	}
}
