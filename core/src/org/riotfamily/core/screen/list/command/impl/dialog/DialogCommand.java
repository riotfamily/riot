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
package org.riotfamily.core.screen.list.command.impl.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.HashUtils;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.DialogResult;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class DialogCommand extends AbstractCommand
		implements ClickListener {

	public CommandResult execute(CommandContext context, Selection selection) {
		Form form = createForm(context, selection);
		
		form.setAttribute("commandId", context.getCommandId());
		form.setAttribute("selection", selection);
		form.setAttribute("listStateKey", context.getListKey());
		
		String sessionKey = HashUtils.md5(context.getListKey() + getName());
		context.getRequest().getSession().setAttribute(sessionKey, form);
		form.setAttribute("sessionKey", sessionKey);
		
		String formUrl = HandlerUrlUtils.getUrl(context.getRequest(), 
				"commandDialogController", sessionKey);

		form.setFormContext(context.createFormContext(formUrl));
		form.init();
		
		StringWriter sw = new StringWriter();
		form.render(new PrintWriter(sw));

		return new DialogResult().setContent(sw.toString()).setCloseButton(true);
	}
		
	public abstract Form createForm(CommandContext context, Selection selection);
	
	public CommandResult handleInput(CommandContext context, Selection selection,
			Object input, String button) {
		
		return null;
	}
	
	public void clicked(ClickEvent event) {
		Form f = event.getSource().getForm();
		if (!f.hasErrors()) {
			f.getFormListener().eval(
				"riot.window.closeAll(); list.handleInput('" 
						+ f.getAttribute("sessionKey") + "');"
			);
		}	
	}
	
	protected void addButton(Form form, String name) {
		Button button = new Button();
		button.setParamName(name);
		button.setLabelKey("button." + name);
		button.setDefaultLabel(FormatUtils.camelToTitleCase(name));
		button.addClickListener(this);
		button.setPartitialSubmit(form.getId());
		form.addButton(button);
	}
	
}
