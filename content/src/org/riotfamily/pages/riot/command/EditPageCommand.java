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
package org.riotfamily.pages.riot.command;

import org.riotfamily.core.screen.DefaultScreenContext;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenRepository;
import org.riotfamily.core.screen.form.FormScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractSingleItemCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.ContentPage;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.VirtualPage;

public class EditPageCommand extends AbstractSingleItemCommand<Page>{
	
	private FormRepository formRepository;
	
	private ScreenRepository screenRepository;
	
	public EditPageCommand(ScreenRepository screenRepository, FormRepository formRepository) {
		this.screenRepository = screenRepository;
		this.formRepository = formRepository;
	}

	@Override
	protected String getName() {
		return "edit";
	}
	@Override
	protected String getIcon() {
		return "pencil";
	}
	
	@Override
	protected boolean isEnabled(CommandContext context, Page page) {
		if (page instanceof VirtualPage) {
			VirtualPage virualPage = (VirtualPage) page;
			Object object = virualPage.getObject();
			return formRepository.findFormId(object.getClass()) != null;
		}
		return page instanceof ContentPage;
	}
	
	@Override
	protected CommandResult execute(CommandContext context, Page page) {
		ScreenContext targetContext = null;
		if (page instanceof ContentPage) {
			targetContext = context.getScreenContext().createItemContext(page); 
		}
		else if (page instanceof VirtualPage) {
			VirtualPage virualPage = (VirtualPage) page;
			Object object = virualPage.getObject();
			String formId = formRepository.findFormId(object.getClass());
			if (formId != null) {
				FormScreen formScreen = screenRepository.findFormScreen(formId);
				ScreenContext screenContext = new DefaultScreenContext(formScreen, 
						object, context.getParent(), false, context.getScreenContext());
				
				targetContext = screenContext.createItemContext(object);
			}
		}
		return new GotoUrlResult(targetContext);
	}

}
