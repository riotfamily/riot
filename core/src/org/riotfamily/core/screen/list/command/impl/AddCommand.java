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
package org.riotfamily.core.screen.list.command.impl;

import java.util.HashMap;
import java.util.Map;

import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.core.dao.Constraints;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.dao.Tree;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.form.FormScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.dialog.DialogCommand;
import org.riotfamily.core.screen.list.command.impl.support.RootSelectionItem;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.element.select.SelectBox;

public class AddCommand extends DialogCommand {
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		RiotDao dao = context.getScreen().getDao();
		if (dao instanceof Constraints) {
			Constraints cd = (Constraints) dao;
			return cd.canAdd(getParent(context, selection).getObject());
		}
		return true;
	}
	
	@Override
	public CommandResult execute(CommandContext context, Selection selection) {
		SelectionItem parent = getParent(context, selection);
		
		ScreenContext itemContext = context.getScreenContext()
				.createNewItemContext(parent.getObject());
		
		if (itemContext.getScreen() instanceof FormScreen) {
			FormScreen formScreen = (FormScreen) itemContext.getScreen();
			if (formScreen.isFormChooser()) {
				return super.execute(context, selection);
			}
		}
		
		return new GotoUrlResult(itemContext);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public CommandResult handleInput(CommandContext context,
			Selection selection, Object input, String button) {
		
		Map<String, String> model = (Map<String, String>) input;
		ScreenContext itemContext = context.getScreenContext()
				.createNewItemContext(getParent(context, selection).getObject());
		
		StringBuffer url = new StringBuffer();
		url.append(itemContext.getLink().getUrl());
		ServletUtils.appendParameter(url, "formId", model.get("type"));

		return new GotoUrlResult(itemContext.getRequest(), url.toString());
	}
	
	@Override
	public Form createForm(CommandContext context, Selection selection) {
		ScreenContext itemContext = context.getScreenContext()
			.createNewItemContext(getParent(context, selection).getObject());

		FormScreen formScreen = (FormScreen) itemContext.getScreen(); 
		
		String messageKey = context.getScreen().getId() + ".form.add";
		Form form = new Form(HashMap.class);
		form.setId(messageKey);
		SelectBox sb = new SelectBox();
		sb.setRequired(true);
		sb.setAppendLabel(true);
		sb.setLabelMessageKey(messageKey + ".");
		sb.setOptions(formScreen.getFormIds());
		form.addElement(sb, "type");
		addButton(form, "create");
		return form;
	}
	
	private SelectionItem getParent(CommandContext context, Selection selection) {
		if (context.getScreen().getDao() instanceof Tree) {
			if (selection.size() == 1) {
				return selection.getSingleItem();
			}
		}
		return new RootSelectionItem();
	}
	
	

}
