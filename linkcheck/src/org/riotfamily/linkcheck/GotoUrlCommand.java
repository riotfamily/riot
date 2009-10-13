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
package org.riotfamily.linkcheck;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractSingleItemCommand;
import org.riotfamily.core.screen.list.command.result.PopupResult;

public class GotoUrlCommand extends AbstractSingleItemCommand<Object> {

	private String urlProperty;
	
	public void setUrlProperty(String urlProperty) {
		this.urlProperty = urlProperty;
	}

	@Override
	protected boolean isShowOnForm(CommandContext context) {
		return true;
	}

	@Override
	protected String getIcon(String action) {
		return "application_go";
	}

	@Override
	protected CommandResult execute(CommandContext context, Object item) {
		String url = PropertyUtils.getPropertyAsString(item, urlProperty);
		return new PopupResult(url);
	}
	
}
